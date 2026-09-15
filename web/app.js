(() => {
  const key = 'todo-terminal.tasks.v1';
  const terminal = document.querySelector('#terminal');
  const input = document.querySelector('#input');
  const form = document.querySelector('#form');
  const file = document.querySelector('#file');

  function print(text) { if (text === '__CLEAR__') { terminal.innerHTML = ''; return; } const line = document.createElement('div'); line.textContent = text; terminal.append(line); terminal.scrollTop = terminal.scrollHeight; }
  function csvParse(text) {
    const rows = []; let row = [], field = '', quoted = false;
    for (let i=0;i<text.length;i++) { const c=text[i]; if (c === '"') { if (quoted && text[i+1] === '"') { field += '"'; i++; } else quoted = !quoted; } else if (c === ',' && !quoted) { row.push(field); field=''; } else if ((c==='\n' || c==='\r') && !quoted) { if (c==='\r' && text[i+1]==='\n') i++; row.push(field); if (row.some(x=>x.trim())) rows.push(row); row=[]; field=''; } else field += c; }
    if (quoted) throw Error('CSV contains an unclosed quote.'); if (field || row.length) { row.push(field); rows.push(row); }
    if (!rows.length || rows[0].join(',') !== 'id,description,completed') throw Error('CSV must start with id,description,completed.');
    const seen = new Set(); return rows.slice(1).map((r,i) => { if (r.length !== 3) throw Error(`Row ${i+2} must contain 3 columns.`); const id=Number(r[0]); if (!Number.isInteger(id)||id<1||seen.has(id)) throw Error(`Invalid or duplicate ID on row ${i+2}.`); if (r[2] !== 'true' && r[2] !== 'false') throw Error(`Invalid completed value on row ${i+2}.`); seen.add(id); return {id,description:r[1],completed:r[2]==='true'}; });
  }
  function csvEscape(v) { v=String(v); return /[",\n\r]/.test(v) ? `"${v.replaceAll('"','""')}"` : v; }
  function csv(tasks) { return ['id,description,completed', ...tasks.map(t => [t.id,t.description,t.completed].map(csvEscape).join(','))].join('\n')+'\n'; }
  function save(tasks) { localStorage.setItem(key, JSON.stringify(tasks)); }
  function load() { try { const saved=localStorage.getItem(key); if (saved) return JSON.parse(saved); } catch (_) {} return null; }
  function send(command) { print('> '+command); const response=typeof window.execute === 'function' ? window.execute(command) : 'TeaVM is not loaded. Build the project first.'; print(response); if (response !== '__CLEAR__') saveCurrent(); }
  function saveCurrent() { /* Java state is the source of truth; browser state is updated by CSV bridge below. */ }
  async function initialise() { const saved=load(); if (saved) { print('Restored saved tasks.'); return; } try { const text=await fetch('data/tasks.csv').then(r=>r.text()); const tasks=csvParse(text); save(tasks); print('Loaded default tasks from data/tasks.csv.'); } catch(e) { print('Could not load default CSV: '+e.message); } }
  form.addEventListener('submit', e => { e.preventDefault(); const value=input.value.trim(); if(value){send(value); input.value='';} input.focus(); });
  document.querySelector('#export').onclick=()=>{ const tasks=load()||[]; const a=document.createElement('a'); a.href=URL.createObjectURL(new Blob([csv(tasks)],{type:'text/csv'})); a.download='tasks.csv'; a.click(); URL.revokeObjectURL(a.href); print('Exported tasks.csv.'); };
  document.querySelector('#import').onclick=()=>file.click();
  file.onchange=()=>{ const selected=file.files[0]; if(!selected)return; const reader=new FileReader(); reader.onload=()=>{try{const tasks=csvParse(reader.result);save(tasks);print(`Imported ${tasks.length} task(s). Refresh to load them into Java.`);}catch(e){print('Import failed: '+e.message);}}; reader.readAsText(selected); file.value=''; };
  document.querySelector('#reset').onclick=async()=>{if(!confirm('Replace your saved tasks with the repository CSV?'))return; localStorage.removeItem(key); await initialise(); print('Reset complete. Refresh to start Java with these tasks.');};
  initialise();
})();
