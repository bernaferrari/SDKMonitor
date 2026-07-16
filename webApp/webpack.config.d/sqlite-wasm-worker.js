// Room's Kotlin/Wasm worker URL is constructed with `new URL(...)` inside a raw JS
// interop string, so webpack cannot discover it automatically. Bundle the local NPM
// worker as a second entry and preserve the URL expected by WebWorkerSQLiteDriver.
config.entry = config.entry || {};
config.entry['sqlite-wasm-worker'] = {
  import: require.resolve('sqlite-wasm-worker/worker.js'),
  filename: 'sqlite-wasm-worker/worker.js',
};

// SQLite's OPFS VFS uses SharedArrayBuffer and is only installed in a
// cross-origin-isolated context. Keep the development server aligned with the
// production headers in vercel.json so `sqlite3.oo1.OpfsDb` is available.
config.devServer = config.devServer || {};
config.devServer.headers = {
  ...(config.devServer.headers || {}),
  'Cross-Origin-Opener-Policy': 'same-origin',
  'Cross-Origin-Embedder-Policy': 'require-corp',
};
