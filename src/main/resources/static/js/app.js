const API = '/api';
let allCourses = [];
let allStudents = [];

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

// ===== NAVIGATION =====
function showPage(name, push = true) {
  if (name === 'hero') {
    window.scrollTo({ top: 0, behavior: 'smooth' });
    return;
  }
  document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
  document.querySelectorAll('nav a').forEach(a => a.classList.remove('active'));
  const page = $(`page-${name}`);
  if (page) page.classList.add('active');
  const navLink = document.querySelector(`nav a[onclick*="'${name}'"]`);
  if (navLink) navLink.classList.add('active');
  if (name === 'catalog') loadCourses();
  if (name === 'students') loadStudents();
}

// Sticky header
let heroDone = false;
window.addEventListener('scroll', () => {
  const hero = $('#hero');
  const header = $('#header');
  if (!hero || !header) return;
  const heroBottom = hero.offsetTop + hero.offsetHeight;
  if (window.scrollY > heroBottom - 60) {
    if (!heroDone) { heroDone = true; header.classList.add('visible'); }
  } else {
    if (heroDone) { heroDone = false; header.classList.remove('visible'); }
  }
});

// ===== TOAST =====
function toast(msg, type = 'success') {
  let el = $('toast');
  if (!el) {
    el = document.createElement('div');
    el.id = 'toast';
    el.className = 'toast';
    document.body.appendChild(el);
  }
  el.textContent = msg;
  el.className = 'toast ' + type + ' show';
  if (window.toastTimer) clearTimeout(window.toastTimer);
  window.toastTimer = setTimeout(() => el.classList.remove('show'), 3500);
}

// ===== MODAL =====
function showModal(html) {
  $('modal-body').innerHTML = html;
  $('modal').classList.add('active');
}
function closeModal() { $('modal').classList.remove('active'); }

// ===== PREVIEW =====
function showPreview(html) {
  $('preview-body').innerHTML = html;
  $('preview-modal').classList.add('active');
}
function closePreview() { $('preview-modal').classList.remove('active'); }

// ===== COURSES =====
async function loadCourses() {
  const el = $('course-list');
  el.innerHTML = '<div class="loading"><i class="fas fa-spinner"></i>Loading courses...</div>';
  try {
    allCourses = await api('/courses');
    if (!allCourses.length) {
      el.innerHTML = '<div class="empty"><i class="fas fa-book-open"></i><p>No courses yet</p></div>';
      return;
    }
    renderCourses(allCourses);
  } catch (e) {
    el.innerHTML = `<div class="empty"><i class="fas fa-exclamation-triangle"></i><p>${esc(e.message)}</p></div>`;
  }
}

function renderCourses(courses) {
  const el = $('course-list');
  const icons = ['fa-java', 'fa-leaf', 'fa-database', 'fa-code', 'fa-server', 'fa-cloud'];
  el.innerHTML = courses.map((c, i) => `
    <div class="course-card" onclick="loadCourseDetail(${c.id})">
      <div class="course-icon"><i class="fab ${icons[i % icons.length]}"></i></div>
      <h3>${esc(c.title)}</h3>
      <div class="desc">${esc(c.description || 'No description')}</div>
      <div class="meta">
        <span><i class="far fa-calendar"></i> ${c.createdAt ? c.createdAt.slice(0, 10) : '—'}</span>
      </div>
    </div>
  `).join('');
}

function filterCourses() {
  const q = $('search-input').value.toLowerCase().trim();
  if (!q) return renderCourses(allCourses);
  const filtered = allCourses.filter(c =>
    c.title.toLowerCase().includes(q) ||
    (c.description && c.description.toLowerCase().includes(q))
  );
  const el = $('course-list');
  if (!filtered.length) {
    el.innerHTML = `<div class="empty"><i class="fas fa-search"></i><p>No courses matching "${esc(q)}"</p></div>`;
  } else {
    renderCourses(filtered);
  }
}

async function loadCourseDetail(id) {
  showPage('detail', false);
  const el = $('course-detail');
  el.innerHTML = '<div class="loading"><i class="fas fa-spinner"></i>Loading...</div>';
  try {
    const c = await api('/courses/' + id);
    const totalLessons = c.modules.reduce((s, m) => s + (m.lessons || []).length, 0);
    const totalDuration = c.modules.reduce((s, m) =>
      s + (m.lessons || []).reduce((ss, l) => ss + (l.durationMinutes || 0), 0), 0);

    el.innerHTML = `
      <div class="detail-hero">
        <h2>${esc(c.title)}</h2>
        <p>${esc(c.description || '')}</p>
        <div class="stats">
          <span><i class="fas fa-layer-group"></i> ${c.modules.length} modules</span>
          <span><i class="fas fa-file"></i> ${totalLessons} lessons</span>
          <span><i class="far fa-clock"></i> ${totalDuration} min total</span>
        </div>
      </div>

      ${c.modules.map((m, mi) => `
        <div class="module-card">
          <div class="module-header" onclick="toggleLessons(this)">
            <h3><i class="fas fa-folder${mi === 0 ? '-open' : ''}"></i> ${esc(m.title)}</h3>
            <span class="badge">${(m.lessons || []).length} lessons</span>
          </div>
          <div class="lesson-list ${mi === 0 ? 'open' : ''}">
            ${(m.lessons || []).map(l => `
              <div class="lesson-item" onclick="previewLesson(${l.id})">
                <span class="title">
                  <i class="fas fa-play-circle"></i> ${esc(l.title)}
                </span>
                <span class="duration"><i class="far fa-clock"></i> ${l.durationMinutes} min</span>
              </div>
            `).join('') || '<div class="lesson-item" style="color:var(--text3)">No lessons</div>'}
          </div>
        </div>
      `).join('')}

      <div class="enroll-section">
        <h3><i class="fas fa-user-plus"></i> Enroll a Student</h3>
        <div class="enroll-row">
          <select id="enroll-student">
            <option value="">Select student...</option>
          </select>
          <button class="btn btn-success" onclick="enrollStudent(${c.id})">
            <i class="fas fa-check"></i> Enroll
          </button>
        </div>
      </div>
    `;
    await loadEnrollSelect();
  } catch (e) {
    el.innerHTML = `<div class="empty"><i class="fas fa-exclamation-triangle"></i><p>${esc(e.message)}</p></div>`;
  }
}

function toggleLessons(header) {
  const list = header.nextElementSibling;
  const icon = header.querySelector('h3 i');
  list.classList.toggle('open');
  icon.className = list.classList.contains('open') ? 'fas fa-folder-open' : 'fas fa-folder';
}

async function previewLesson(lessonId) {
  try {
    const lesson = await api('/lessons/' + lessonId);
    const content = lesson.content || 'No content available for this lesson.';
    showPreview(`
      <div class="preview-header">
        <h3>${esc(lesson.title)}</h3>
        <div class="meta">
          <span><i class="far fa-clock"></i> ${lesson.durationMinutes} min</span>
        </div>
      </div>
      <div class="preview-body">${esc(content)}</div>
    `);
  } catch (e) {
    toast('Failed to load lesson preview', 'error');
  }
}

async function loadEnrollSelect() {
  try {
    allStudents = await api('/students');
    const sel = $('enroll-student');
    if (!sel) return;
    sel.innerHTML = '<option value="">Select student...</option>' +
      allStudents.map(s => `<option value="${s.id}">${esc(s.name)} (${esc(s.email)})</option>`).join('');
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

// ===== STUDENTS =====
async function loadStudents() {
  const el = $('student-list');
  el.innerHTML = '<div class="loading"><i class="fas fa-spinner"></i>Loading...</div>';
  try {
    allStudents = await api('/students');
    if (!allStudents.length) {
      el.innerHTML = '<div class="empty"><i class="fas fa-users"></i><p>No students yet</p></div>';
      return;
    }
    el.innerHTML = '<div class="student-grid">' +
      allStudents.map(s => `
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
      <input type="text" id="student-name" placeholder="e.g. Jane Doe">
    </div>
    <div class="form-group">
      <label>Email</label>
      <input type="email" id="student-email" placeholder="e.g. jane@example.com">
    </div>
    <button class="btn" onclick="addStudent()"><i class="fas fa-check"></i> Create Student</button>
  `);
  setTimeout(() => { const el = $('student-name'); if (el) el.focus(); }, 100);
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
    <h3><i class="fas fa-plus-circle"></i> New Course</h3>
    <div class="form-group">
      <label>Course Title</label>
      <input type="text" id="course-title" placeholder="e.g. Advanced Kubernetes">
    </div>
    <div class="form-group">
      <label>Description</label>
      <textarea id="course-desc" placeholder="Course description..."></textarea>
    </div>
    <button class="btn" onclick="addCourse()"><i class="fas fa-check"></i> Create Course</button>
  `);
  setTimeout(() => { const el = $('course-title'); if (el) el.focus(); }, 100);
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

// ===== HERO STATS =====
async function loadStats() {
  try {
    const [courses, students] = await Promise.all([
      api('/courses'),
      api('/students')
    ]);
    let mods = 0, less = 0;
    for (const c of courses) {
      try {
        const detail = await api('/courses/' + c.id);
        for (const m of detail.modules) {
          mods++;
          less += (m.lessons || []).length;
        }
      } catch(_) {}
    }
    animateNum('stat-courses', courses.length);
    animateNum('stat-modules', mods);
    animateNum('stat-lessons', less);
    animateNum('stat-students', students.length);
  } catch(_) {}
}

function animateNum(id, target) {
  const el = $(id);
  if (!el) return;
  let current = 0;
  const step = Math.max(1, Math.ceil(target / 30));
  const interval = setInterval(() => {
    current += step;
    if (current >= target) { current = target; clearInterval(interval); }
    el.textContent = current;
  }, 40);
}

// ===== HELPERS =====
function esc(s) {
  if (!s) return '';
  const d = document.createElement('div');
  d.textContent = s;
  return d.innerHTML;
}

// ===== INIT =====
loadStats();
