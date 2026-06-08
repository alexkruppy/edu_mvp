const API = '/api';

async function api(path, opts = {}) {
  const res = await fetch(API + path, {
    headers: { 'Content-Type': 'application/json', ...opts.headers },
    ...opts
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ message: res.statusText }));
    throw new Error(err.message || `HTTP ${res.status}`);
  }
  return res.status === 204 ? null : res.json();
}

function $(id) { return document.getElementById(id); }

// --- Navigation ---
function showPage(name) {
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('nav a').forEach(a => a.classList.remove('active'));
  $(`page-${name}`).classList.add('active');
  document.querySelector(`nav a[onclick*="'${name}'"]`)?.classList.add('active');

  if (name === 'catalog') loadCourses();
  if (name === 'students') loadStudents();
}

// --- Toast ---
function toast(msg, type = 'success') {
  const el = $('toast');
  el.textContent = msg;
  el.className = 'toast ' + type + ' show';
  setTimeout(() => el.classList.remove('show'), 3000);
}

// --- Modal ---
function showModal(html) {
  $('modal-body').innerHTML = html;
  $('modal').classList.add('active');
}
function closeModal() { $('modal').classList.remove('active'); }
$('modal').addEventListener('click', e => { if (e.target === $('modal')) closeModal(); });

// --- Courses ---
async function loadCourses() {
  const el = $('course-list');
  el.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i>Loading courses...</div>';
  try {
    const courses = await api('/courses');
    if (!courses.length) {
      el.innerHTML = '<div class="empty"><i class="fas fa-book-open"></i><p>No courses yet</p></div>';
      return;
    }
    el.innerHTML = courses.map(c => `
      <div class="course-card" onclick="loadCourseDetail(${c.id})">
        <h3>${esc(c.title)}</h3>
        <p>${esc(c.description || 'No description')}</p>
        <div class="meta">
          <span><i class="far fa-calendar"></i> ${c.createdAt ? c.createdAt.slice(0, 10) : '—'}</span>
        </div>
      </div>
    `).join('');
  } catch (e) {
    el.innerHTML = `<div class="empty"><i class="fas fa-exclamation-triangle"></i><p>${esc(e.message)}</p></div>`;
  }
}

async function loadCourseDetail(id) {
  showPage('detail');
  const el = $('course-detail');
  el.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i>Loading...</div>';
  try {
    const c = await api('/courses/' + id);
    const totalLessons = c.modules.reduce((s, m) => s + (m.lessons?.length || 0), 0);

    el.innerHTML = `
      <div class="detail-header">
        <h2>${esc(c.title)}</h2>
        <p>${esc(c.description || '')}</p>
        <p style="margin-top:8px;font-size:0.85rem;color:var(--text2)">
          <i class="fas fa-layer-group"></i> ${c.modules.length} modules · 
          <i class="fas fa-file"></i> ${totalLessons} lessons
        </p>
      </div>
      ${c.modules.map((m, i) => `
        <div class="module-card">
          <div class="module-header" onclick="this.nextElementSibling.classList.toggle('active')">
            <h3><i class="fas fa-folder"></i> ${esc(m.title)}</h3>
            <span class="badge">${m.lessons?.length || 0} lessons</span>
          </div>
          <div class="lesson-list" style="display:${i === 0 ? 'block' : 'none'}">
            ${(m.lessons || []).map(l => `
              <div class="lesson-item">
                <span class="title">
                  <i class="fas fa-play-circle"></i> ${esc(l.title)}
                </span>
                <span class="duration"><i class="far fa-clock"></i> ${l.durationMinutes} min</span>
              </div>
            `).join('') || '<div class="lesson-item" style="color:var(--text2)">No lessons</div>'}
          </div>
        </div>
      `).join('')}

      <div class="enroll-section">
        <select id="enroll-student">
          <option value="">Select student...</option>
        </select>
        <button class="btn btn-success" onclick="enrollStudent(${c.id})">
          <i class="fas fa-user-plus"></i> Enroll
        </button>
      </div>
    `;
    loadEnrollSelect();
  } catch (e) {
    el.innerHTML = `<div class="empty"><i class="fas fa-exclamation-triangle"></i><p>${esc(e.message)}</p></div>`;
  }
}

async function loadEnrollSelect() {
  try {
    const students = await api('/students');
    const sel = $('enroll-student');
    sel.innerHTML = '<option value="">Select student...</option>' +
      students.map(s => `<option value="${s.id}">${esc(s.name)} (${esc(s.email)})</option>`).join('');
  } catch (_) {}
}

async function enrollStudent(courseId) {
  const studentId = $('enroll-student').value;
  if (!studentId) return toast('Select a student first', 'error');
  try {
    const res = await api(`/courses/${courseId}/enroll?studentId=${studentId}`, { method: 'POST' });
    toast(res.message || 'Enrolled successfully!');
  } catch (e) {
    toast(e.message, 'error');
  }
}

// --- Students ---
async function loadStudents() {
  const el = $('student-list');
  el.innerHTML = '<div class="loading"><i class="fas fa-spinner fa-spin"></i>Loading...</div>';
  try {
    const students = await api('/students');
    if (!students.length) {
      el.innerHTML = '<div class="empty"><i class="fas fa-users"></i><p>No students yet</p></div>';
      return;
    }
    el.innerHTML = '<div class="student-grid">' +
      students.map(s => `
        <div class="student-card">
          <div class="avatar">${s.name.charAt(0).toUpperCase()}</div>
          <div class="info">
            <h4>${esc(s.name)}</h4>
            <span>${esc(s.email)}</span>
          </div>
        </div>
      `).join('') + '</div>';
  } catch (e) {
    el.innerHTML = `<div class="empty"><i class="fas fa-exclamation-triangle"></i><p>${esc(e.message)}</p></div>`;
  }
}

function showAddStudentForm() {
  showModal(`
    <h3><i class="fas fa-user-plus"></i> Add Student</h3>
    <div class="form-group">
      <label>Full Name</label>
      <input type="text" id="student-name" placeholder="John Doe">
    </div>
    <div class="form-group">
      <label>Email</label>
      <input type="email" id="student-email" placeholder="john@example.com">
    </div>
    <button class="btn" onclick="addStudent()"><i class="fas fa-check"></i> Create</button>
  `);
}

async function addStudent() {
  const name = $('student-name').value.trim();
  const email = $('student-email').value.trim();
  if (!name || !email) return toast('Fill all fields', 'error');
  try {
    await api('/students', {
      method: 'POST',
      body: JSON.stringify({ name, email })
    });
    closeModal();
    toast('Student created!');
    loadStudents();
  } catch (e) {
    toast(e.message, 'error');
  }
}

function showAddCourseForm() {
  showModal(`
    <h3><i class="fas fa-plus-circle"></i> Add Course</h3>
    <div class="form-group">
      <label>Title</label>
      <input type="text" id="course-title" placeholder="Course title">
    </div>
    <div class="form-group">
      <label>Description</label>
      <textarea id="course-desc" placeholder="Course description"></textarea>
    </div>
    <button class="btn" onclick="addCourse()"><i class="fas fa-check"></i> Create</button>
  `);
}

async function addCourse() {
  const title = $('course-title').value.trim();
  const description = $('course-desc').value.trim();
  if (!title) return toast('Title is required', 'error');
  try {
    await api('/courses', {
      method: 'POST',
      body: JSON.stringify({ title, description })
    });
    closeModal();
    toast('Course created!');
    loadCourses();
  } catch (e) {
    toast(e.message, 'error');
  }
}

// --- Helpers ---
function esc(s) {
  if (!s) return '';
  const d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}

// --- Init ---
showPage('catalog');
