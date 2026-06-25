const vscode = require('vscode');
const path = require('path');
const fs = require('fs');

function cfg() {
  return vscode.workspace.getConfiguration('atRef');
}

function findGitRoot(filePath) {
  let dir = path.dirname(filePath);
  // Guard against infinite loops on root.
  for (let i = 0; i < 64; i++) {
    if (fs.existsSync(path.join(dir, '.git'))) return dir;
    const parent = path.dirname(dir);
    if (parent === dir) return null;
    dir = parent;
  }
  return null;
}

function computePath(uri, style) {
  const fsPath = uri.fsPath;
  switch (style) {
    case 'absolute':
      return fsPath;
    case 'basename':
      return path.basename(fsPath);
    case 'git': {
      const gitRoot = findGitRoot(fsPath);
      if (gitRoot) return path.relative(gitRoot, fsPath);
      // Fall through to workspace if no git root found.
    }
    // eslint-disable-next-line no-fallthrough
    case 'workspace':
    default: {
      const ws = vscode.workspace.getWorkspaceFolder(uri);
      if (ws) return path.relative(ws.uri.fsPath, fsPath);
      return path.basename(fsPath);
    }
  }
}

function toPosix(p) {
  return p.split(path.sep).join('/');
}

function selectionLineSpan(selection) {
  if (!selection) return null;
  const start = selection.start.line + 1;
  const endRaw = selection.end.line + 1;
  // Selection ending at column 0 of the next line shouldn't count that next line.
  const end = selection.end.character === 0 && endRaw > start ? endRaw - 1 : endRaw;
  return { start, end };
}

function lineSuffix(span, rangeSep) {
  if (!span) return '';
  if (span.start === span.end) return `:${span.start}`;
  return `:${span.start}${rangeSep}${span.end}`;
}

function formatAt(pathStr, span, rangeSep) {
  return `@${pathStr}${lineSuffix(span, rangeSep)}`;
}

function formatColon(pathStr, span, rangeSep) {
  return `${pathStr}${lineSuffix(span, rangeSep)}`;
}

function formatMarkdown(pathStr, span, rangeSep) {
  if (!span) return `[${pathStr}](${pathStr})`;
  const label = `${pathStr}${lineSuffix(span, rangeSep)}`;
  const hash =
    span.start === span.end
      ? `#L${span.start}`
      : `#L${span.start}-L${span.end}`;
  return `[${label}](${pathStr}${hash})`;
}

const EXT_TO_LANG = {
  '.py': 'python',
  '.ts': 'typescript', '.tsx': 'tsx', '.mts': 'typescript', '.cts': 'typescript',
  '.js': 'javascript', '.jsx': 'jsx', '.mjs': 'javascript', '.cjs': 'javascript',
  '.go': 'go',
  '.rs': 'rust',
  '.java': 'java', '.kt': 'kotlin', '.kts': 'kotlin',
  '.swift': 'swift',
  '.rb': 'ruby',
  '.php': 'php',
  '.cs': 'csharp', '.fs': 'fsharp',
  '.c': 'c', '.h': 'c',
  '.cpp': 'cpp', '.cc': 'cpp', '.cxx': 'cpp', '.hpp': 'cpp', '.hxx': 'cpp',
  '.m': 'objective-c', '.mm': 'objective-cpp',
  '.sh': 'bash', '.bash': 'bash', '.zsh': 'zsh', '.fish': 'fish',
  '.ps1': 'powershell',
  '.lua': 'lua',
  '.r': 'r',
  '.md': 'markdown', '.mdx': 'mdx',
  '.json': 'json', '.json5': 'json5', '.jsonc': 'jsonc',
  '.yaml': 'yaml', '.yml': 'yaml',
  '.toml': 'toml',
  '.xml': 'xml',
  '.html': 'html', '.htm': 'html',
  '.css': 'css', '.scss': 'scss', '.sass': 'sass', '.less': 'less',
  '.vue': 'vue',
  '.svelte': 'svelte',
  '.sql': 'sql',
  '.dockerfile': 'dockerfile',
  '.tf': 'terraform',
  '.proto': 'protobuf',
  '.graphql': 'graphql', '.gql': 'graphql',
};

function resolveLang(doc) {
  const id = doc.languageId;
  if (id && id !== 'plaintext') return id;
  const ext = path.extname(doc.uri.fsPath).toLowerCase();
  return EXT_TO_LANG[ext] || '';
}

function withLineNumbers(text, startLine) {
  const lines = text.split('\n');
  const lastLine = startLine + lines.length - 1;
  const width = String(lastLine).length;
  return lines
    .map((line, i) => `${String(startLine + i).padStart(width, ' ')}  ${line}`)
    .join('\n');
}

function formatWithCode(pathStr, span, rangeSep, doc, selection) {
  const ref = formatAt(pathStr, span, rangeSep);
  if (!span || !doc) return ref;

  const c = cfg();
  const includeLineNumbers = c.get('codeBlockLineNumbers', true);
  const contextLines = Math.max(0, c.get('codeBlockContextLines', 0));

  // Convert 1-based span to 0-based document lines.
  let blockStart = span.start - 1;
  let blockEnd = span.end - 1;

  if (contextLines > 0) {
    blockStart = Math.max(0, blockStart - contextLines);
    blockEnd = Math.min(doc.lineCount - 1, blockEnd + contextLines);
  }

  const range = new vscode.Range(
    blockStart,
    0,
    blockEnd,
    doc.lineAt(blockEnd).text.length
  );
  let text = doc.getText(range);

  if (includeLineNumbers) {
    text = withLineNumbers(text, blockStart + 1);
  }

  const lang = resolveLang(doc);
  return `${ref}\n\`\`\`${lang}\n${text}\n\`\`\``;
}

function buildReference(formatKind, uri, selection, doc) {
  const c = cfg();
  const pathStyle = formatKind === 'absolute' ? 'absolute' : c.get('pathStyle', 'workspace');
  const rangeSep = c.get('rangeSeparator', '-');
  const usePosix = c.get('usePosixSeparators', true);

  let p = computePath(uri, pathStyle);
  if (usePosix) p = toPosix(p);

  const span = selectionLineSpan(selection);

  switch (formatKind) {
    case 'at':
      return formatAt(p, span, rangeSep);
    case 'colon':
      return formatColon(p, span, rangeSep);
    case 'markdown':
      return formatMarkdown(p, span, rangeSep);
    case 'absolute':
      return formatColon(p, span, rangeSep);
    case 'withCode':
      return formatWithCode(p, span, rangeSep, doc, selection);
    default:
      return formatAt(p, span, rangeSep);
  }
}

function resolveTargetFromArgs(resourceUri) {
  const editor = vscode.window.activeTextEditor;

  if (resourceUri && resourceUri.scheme === 'file') {
    const doc =
      editor && editor.document.uri.toString() === resourceUri.toString()
        ? editor.document
        : null;
    const selections =
      editor && editor.document.uri.toString() === resourceUri.toString()
        ? editor.selections
        : null;
    return { uri: resourceUri, doc, selections };
  }

  if (editor) {
    return {
      uri: editor.document.uri,
      doc: editor.document,
      selections: editor.selections,
    };
  }

  return null;
}

function notify(message) {
  const where = cfg().get('notification', 'statusBar');
  if (where === 'info') {
    vscode.window.showInformationMessage(message);
  } else if (where === 'statusBar') {
    vscode.window.setStatusBarMessage(message, 2500);
  }
}

async function copyHandler(formatKind, resourceUri) {
  const target = resolveTargetFromArgs(resourceUri);
  if (!target) {
    vscode.window.showWarningMessage(
      'AtRef: no active editor or selected file.'
    );
    return;
  }

  const { uri, doc, selections } = target;

  if (uri.scheme !== 'file') {
    vscode.window.showWarningMessage(
      `AtRef: unsupported file scheme "${uri.scheme}" (e.g. untitled or remote without a local path).`
    );
    return;
  }

  let refs;
  if (selections && selections.length > 0) {
    // Deduplicate identical references that arise when multiple cursors sit on the same line.
    const seen = new Set();
    refs = [];
    for (const sel of selections) {
      const r = buildReference(formatKind, uri, sel, doc);
      if (!seen.has(r)) {
        seen.add(r);
        refs.push(r);
      }
    }
  } else {
    refs = [buildReference(formatKind, uri, null, doc)];
  }

  const joiner = cfg().get('multiSelectionSeparator', '\n');
  const text = refs.join(joiner);

  await vscode.env.clipboard.writeText(text);

  const preview = refs.length > 1 ? `${refs[0]}  (+${refs.length - 1} more)` : refs[0];
  notify(`Copied ${preview}`);
}

function activate(context) {
  const register = (id, kind) =>
    context.subscriptions.push(
      vscode.commands.registerCommand(id, (resourceUri) => copyHandler(kind, resourceUri))
    );

  // Default command — uses the configured defaultFormat.
  context.subscriptions.push(
    vscode.commands.registerCommand('atRef.copy', (resourceUri) => {
      const kind = cfg().get('defaultFormat', 'at');
      return copyHandler(kind, resourceUri);
    })
  );

  register('atRef.copyAsAtRef', 'at');
  register('atRef.copyAsMarkdownLink', 'markdown');
  register('atRef.copyAsColon', 'colon');
  register('atRef.copyWithCode', 'withCode');
  register('atRef.copyAbsolutePath', 'absolute');
}

function deactivate() {}

module.exports = { activate, deactivate };
