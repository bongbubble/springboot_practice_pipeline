<template>
  <div class="app">
    <!-- Navigation -->
    <nav class="nav">
      <div class="nav-content">
        <div class="nav-logo">사용자 관리</div>
        <div class="nav-links">
          <span class="nav-link">대시보드</span>
          <span class="nav-link">사용자</span>
          <span class="nav-link">설정</span>
        </div>
      </div>
    </nav>

    <!-- Hero -->
    <section class="hero">
      <div class="hero-eyebrow">새롭게 디자인된</div>
      <h1 class="hero-headline">사용자 관리</h1>
      <p class="hero-subhead">효율적인 관리 시스템으로 모든 사용자 정보를 한곳에서 확인하세요.</p>
      <div class="cta-group">
        <button class="btn-primary" @click="openAddModal">사용자 추가</button>
        <button class="btn-secondary" @click="focusSearch">검색하기</button>
      </div>
    </section>

    <!-- Stats -->
    <section class="stats-section">
      <div class="stats-grid">
        <div class="stat-card">
          <div class="stat-number">{{ users.length }}</div>
          <div class="stat-label">전체 사용자</div>
          <div class="stat-change">+12.5%</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ activeUsers }}</div>
          <div class="stat-label">활성 사용자</div>
          <div class="stat-change">접속 중</div>
        </div>
        <div class="stat-card">
          <div class="stat-number">{{ newUsers }}</div>
          <div class="stat-label">신규 가입</div>
          <div class="stat-change">이번 주</div>
        </div>
      </div>
    </section>

    <!-- Main -->
    <section class="main-section">
      <div class="section-header">
        <h2 class="section-title">전체 사용자</h2>
        <button class="btn-add" @click="openAddModal">
          <svg fill="currentColor" viewBox="0 0 20 20">
            <path fill-rule="evenodd" d="M10 3a1 1 0 011 1v5h5a1 1 0 110 2h-5v5a1 1 0 11-2 0v-5H4a1 1 0 110-2h5V4a1 1 0 011-1z" clip-rule="evenodd"/>
          </svg>
          추가
        </button>
      </div>

      <div class="search-wrapper">
        <svg class="search-icon" fill="currentColor" viewBox="0 0 20 20">
          <path fill-rule="evenodd" d="M8 4a4 4 0 100 8 4 4 0 000-8zM2 8a6 6 0 1110.89 3.476l4.817 4.817a1 1 0 01-1.414 1.414l-4.816-4.816A6 6 0 012 8z" clip-rule="evenodd"/>
        </svg>
        <input
          ref="searchInput"
          type="text"
          class="search-input"
          placeholder="사용자 검색"
          v-model="searchQuery"
          @keyup.enter="loadUsers()"
        >
      </div>

      <div v-if="isLoadingUsers" class="user-state-card">
        사용자 데이터를 불러오는 중입니다...
      </div>
      <div v-else-if="filteredUsers.length === 0" class="user-state-card">
        일치하는 사용자가 없습니다.
      </div>
      <div v-else class="user-grid">
        <div
          v-for="(user, index) in filteredUsers"
          :key="user.id"
          class="user-card"
          @click="openPanel(user)"
        >
          <div class="user-header">
            <div class="user-avatar" :style="{ background: avatarColors[index % avatarColors.length] }">
              {{ user.name ? user.name.charAt(0) : '?' }}
            </div>
            <div class="user-info">
              <div class="user-name">{{ user.name }}</div>
              <div class="user-email">{{ user.email }}</div>
            </div>
          </div>
          <div class="user-meta">
            <div class="user-id">#{{ String(user.id).padStart(3, '0') }}</div>
            <div class="user-badge">{{ user.status }}</div>
          </div>
          <div>
            <div class="hobbies-label">관심사</div>
            <div class="hobby-tags">
              <div v-for="hobby in user.hobbies" :key="hobby" class="hobby-tag">
                {{ hobby }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Add Modal -->
    <transition name="modal">
      <div v-if="isAddModalOpen" class="modal-overlay" @click.self="closeAddModal">
        <div class="modal">
          <div class="modal-header">
            <h3 class="modal-title">새 사용자 추가</h3>
            <button class="modal-close" @click="closeAddModal">
              <svg fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
              </svg>
            </button>
          </div>
          <form @submit.prevent="handleAddUser">
            <div class="modal-body">
              <div class="form-group">
                <label class="form-label">이름</label>
                <input type="text" class="form-input" v-model="newUser.name" placeholder="홍길동" required>
              </div>
              <div class="form-group">
                <label class="form-label">이메일</label>
                <input type="email" class="form-input" v-model="newUser.email" placeholder="user@example.com" required>
              </div>
              <div class="form-group">
                <label class="form-label">관심사</label>
                <input type="text" class="form-input" v-model="newUser.hobbiesText" placeholder="독서, 운동, 음악">
                <div class="form-hint">쉼표로 구분하여 입력하세요</div>
              </div>
            </div>
            <div class="modal-footer">
              <button type="button" class="btn-cancel" @click="closeAddModal">취소</button>
              <button type="submit" class="btn-submit" :disabled="isSubmitting">추가하기</button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <!-- Side Panel -->
    <transition name="panel">
      <div v-if="isPanelOpen">
        <div class="panel-overlay" @click="closePanel"></div>
        <div class="side-panel">
          <div class="panel-header">
            <h3 class="panel-title">사용자 정보</h3>
            <button class="panel-close" @click="closePanel">
              <svg fill="currentColor" viewBox="0 0 20 20">
                <path fill-rule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clip-rule="evenodd"/>
              </svg>
            </button>
          </div>
          <div v-if="selectedUser" class="panel-body">
            <div class="panel-user-header">
              <div class="panel-avatar" :style="{ background: getAvatarColor(selectedUser.id) }">
                {{ selectedUser.name ? selectedUser.name.charAt(0) : '?' }}
              </div>
              <div class="panel-user-name">{{ selectedUser.name }}</div>
              <div class="panel-user-email">{{ selectedUser.email }}</div>
            </div>

            <div class="info-section">
              <div class="info-section-title">기본 정보</div>
              <div class="info-list">
                <div class="info-item">
                  <span class="info-label">사용자 ID</span>
                  <span class="info-value">#{{ String(selectedUser.id).padStart(3, '0') }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">이메일</span>
                  <span class="info-value">{{ selectedUser.email }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">가입일</span>
                  <span class="info-value">{{ selectedUser.date }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">상태</span>
                  <span class="info-value">{{ selectedUser.status }}</span>
                </div>
              </div>
            </div>

            <div class="info-section">
              <div class="info-section-title">관심사</div>
              <div class="hobby-tags">
                <div v-for="hobby in selectedUser.hobbies" :key="hobby" class="hobby-tag">
                  {{ hobby }}
                </div>
              </div>
            </div>

            <div class="panel-actions">
              <button class="panel-btn panel-btn-edit" @click="showToast('수정 기능 준비중입니다')">
                정보 수정
              </button>
              <button
                class="panel-btn panel-btn-delete"
                @click="deleteUser(selectedUser)"
                :disabled="isDeleting"
              >
                사용자 삭제
              </button>
            </div>
          </div>
        </div>
      </div>
    </transition>

    <!-- Toast -->
    <transition name="toast">
      <div v-if="toast.visible" class="toast">
        {{ toast.message }}
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import {
  fetchUsers,
  createUser,
  deleteUser as deleteUserApi
} from './services/api.js';

const users = ref([]);
const isLoadingUsers = ref(false);
const isSubmitting = ref(false);
const isDeleting = ref(false);

const searchQuery = ref('');
const searchInput = ref(null);
const isAddModalOpen = ref(false);
const isPanelOpen = ref(false);
const selectedUser = ref(null);

const newUser = ref({
  name: '',
  email: '',
  hobbiesText: ''
});

const toast = ref({
  visible: false,
  message: ''
});

const avatarColors = [
  'linear-gradient(135deg, #0071e3, #5e5ce6)',
  'linear-gradient(135deg, #ff2d55, #ff6482)',
  'linear-gradient(135deg, #30d158, #32d74b)',
  'linear-gradient(135deg, #ff9500, #ffcc00)',
  'linear-gradient(135deg, #af52de, #bf5af2)'
];

const defaultStatus = '활성';
const defaultDateLabel = '가입일 정보 없음';

const formatDate = (value) => {
  if (!value) return defaultDateLabel;
  const parsed = value instanceof Date ? value : new Date(value);
  if (Number.isNaN(parsed.getTime())) return defaultDateLabel;
  return parsed.toISOString().split('T')[0].replace(/-/g, '.');
};

const normaliseUser = (user) => {
  const hobbies = Array.isArray(user.hobbies) ? user.hobbies : [];
  const dateValue = user.date ?? user.createdAt ?? user.created_at ?? null;
  return {
    ...user,
    hobbies,
    status: user.status ?? defaultStatus,
    date: formatDate(dateValue)
  };
};

const loadUsers = async () => {
  isLoadingUsers.value = true;
  try {
    const query = searchQuery.value.trim();
    const data = await fetchUsers(query);
    users.value = data.map(normaliseUser);

    if (selectedUser.value) {
      const updated = users.value.find((user) => user.id === selectedUser.value?.id);
      if (updated) {
        selectedUser.value = updated;
      } else {
        closePanel();
      }
    }
  } catch (error) {
    console.error(error);
    showToast('사용자 목록을 불러오지 못했습니다.');
  } finally {
    isLoadingUsers.value = false;
  }
};

const filteredUsers = computed(() => {
  if (!searchQuery.value) return users.value;

  const query = searchQuery.value.toLowerCase();
  return users.value.filter((user) => {
    const name = user.name?.toLowerCase() ?? '';
    const email = user.email?.toLowerCase() ?? '';
    return name.includes(query) || email.includes(query);
  });
});

const activeUsers = computed(() =>
  users.value.filter((user) => (user.status ?? defaultStatus) === '활성').length
);

const newUsers = computed(() => {
  const oneWeekAgo = new Date();
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7);

  return users.value.filter((user) => {
    if (!user.date || user.date === defaultDateLabel) return false;
    const parsed = new Date(user.date.replace(/\./g, '-'));
    return !Number.isNaN(parsed.getTime()) && parsed >= oneWeekAgo;
  }).length;
});

function openAddModal() {
  isAddModalOpen.value = true;
}

function resetNewUser() {
  newUser.value = {
    name: '',
    email: '',
    hobbiesText: ''
  };
}

function closeAddModal() {
  isAddModalOpen.value = false;
  resetNewUser();
}

async function handleAddUser() {
  const name = newUser.value.name.trim();
  const email = newUser.value.email.trim();

  if (!name || !email) {
    showToast('이름과 이메일을 모두 입력해주세요.');
    return;
  }

  const hobbies = newUser.value.hobbiesText
    ? newUser.value.hobbiesText
        .split(',')
        .map((item) => item.trim())
        .filter(Boolean)
    : [];

  isSubmitting.value = true;
  try {
    await createUser({ name, email, hobbies });
    showToast(`${name}님이 추가되었습니다.`);
    closeAddModal();
    await loadUsers();
  } catch (error) {
    console.error(error);
    showToast('사용자 추가에 실패했습니다.');
  } finally {
    isSubmitting.value = false;
  }
}

function openPanel(user) {
  selectedUser.value = user;
  isPanelOpen.value = true;
}

function closePanel() {
  isPanelOpen.value = false;
  setTimeout(() => {
    selectedUser.value = null;
  }, 300);
}

async function deleteUser(user) {
  if (!user || isDeleting.value) return;
  const confirmed = window.confirm(`정말 ${user.name}님을 삭제하시겠습니까?`);
  if (!confirmed) return;

  isDeleting.value = true;
  try {
    await deleteUserApi(user.id);
    showToast(`${user.name}님이 삭제되었습니다.`);
    await loadUsers();
    closePanel();
  } catch (error) {
    console.error(error);
    showToast('사용자 삭제에 실패했습니다.');
  } finally {
    isDeleting.value = false;
  }
}

function getAvatarColor(userId) {
  if (!users.value.length) return avatarColors[0];
  const index = users.value.findIndex((user) => user.id === userId);
  const safeIndex = index >= 0 ? index : 0;
  return avatarColors[safeIndex % avatarColors.length];
}

function showToast(message) {
  toast.value.message = message;
  toast.value.visible = true;

  setTimeout(() => {
    toast.value.visible = false;
  }, 3000);
}

function focusSearch() {
  searchInput.value?.focus();
}

onMounted(() => {
  loadUsers();
});
</script>

<style scoped>
/* Navigation */
.nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: 44px;
  background: rgba(251, 251, 253, 0.8);
  backdrop-filter: saturate(180%) blur(20px);
  border-bottom: 0.5px solid rgba(0, 0, 0, 0.08);
  z-index: 9999;
}

.nav-content {
  max-width: 980px;
  margin: 0 auto;
  height: 100%;
  padding: 0 22px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.nav-logo {
  font-size: 21px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: -0.02em;
}

.nav-links {
  display: flex;
  gap: 32px;
  align-items: center;
}

.nav-link {
  font-size: 12px;
  color: #1d1d1f;
  opacity: 0.8;
  cursor: pointer;
  transition: opacity 0.3s;
}

.nav-link:hover {
  opacity: 1;
}

/* Hero */
.hero {
  padding: 88px 22px 52px;
  text-align: center;
  background: linear-gradient(180deg, #fbfbfd 0%, #fafafa 100%);
}

.hero-eyebrow {
  font-size: 17px;
  line-height: 1.23536;
  font-weight: 600;
  letter-spacing: -0.022em;
  color: #bf4800;
  margin-bottom: 4px;
}

.hero-headline {
  font-size: 64px;
  line-height: 1.0625;
  font-weight: 600;
  letter-spacing: -0.009em;
  color: #1d1d1f;
  margin-bottom: 12px;
}

.hero-subhead {
  font-size: 24px;
  line-height: 1.16667;
  font-weight: 400;
  letter-spacing: 0.009em;
  color: #86868b;
  max-width: 640px;
  margin: 0 auto 24px;
}

.cta-group {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-primary {
  padding: 12px 22px;
  background: #0071e3;
  color: #fff;
  border-radius: 980px;
  font-size: 17px;
  line-height: 1.17648;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-primary:hover {
  background: #0077ed;
}

.btn-secondary {
  padding: 12px 22px;
  background: transparent;
  color: #0071e3;
  border-radius: 980px;
  font-size: 17px;
  line-height: 1.17648;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
}

.btn-secondary:hover {
  text-decoration: underline;
}

/* Stats */
.stats-section {
  background: #f5f5f7;
  padding: 52px 22px;
}

.stats-grid {
  max-width: 980px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 28px;
}

.stat-card {
  text-align: center;
  padding: 32px 20px;
  background: #fff;
  border-radius: 18px;
  transition: transform 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
}

.stat-number {
  font-size: 56px;
  line-height: 1;
  font-weight: 600;
  letter-spacing: -0.005em;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 17px;
  line-height: 1.47059;
  font-weight: 600;
  letter-spacing: -0.022em;
  color: #1d1d1f;
  margin-bottom: 4px;
}

.stat-change {
  font-size: 14px;
  line-height: 1.42859;
  color: #06c;
}

/* Main Section */
.main-section {
  max-width: 1280px;
  margin: 0 auto;
  padding: 64px 22px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.section-title {
  font-size: 40px;
  line-height: 1.1;
  font-weight: 600;
  letter-spacing: 0;
  color: #1d1d1f;
}

.btn-add {
  padding: 10px 20px;
  background: #0071e3;
  color: #fff;
  border: none;
  border-radius: 980px;
  font-size: 15px;
  font-weight: 400;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-add:hover {
  background: #0077ed;
}

.btn-add svg {
  width: 16px;
  height: 16px;
}

/* Search */
.search-wrapper {
  max-width: 480px;
  margin-bottom: 40px;
  position: relative;
}

.search-input {
  width: 100%;
  height: 36px;
  padding: 0 12px 0 36px;
  background: #fff;
  border: 1px solid #d2d2d7;
  border-radius: 8px;
  font-size: 15px;
  color: #1d1d1f;
  transition: all 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

.search-icon {
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 15px;
  height: 15px;
  opacity: 0.5;
}

.user-state-card {
  padding: 48px 16px;
  text-align: center;
  background: #fff;
  border-radius: 12px;
  border: 1px solid rgba(0, 0, 0, 0.06);
  color: #86868b;
}

/* User Grid */
.user-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.user-card {
  background: #fff;
  border-radius: 12px;
  padding: 24px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 1px solid rgba(0, 0, 0, 0.06);
}

.user-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  border-color: rgba(0, 0, 0, 0.1);
}

.user-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.user-avatar {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  font-weight: 600;
  color: #fff;
  flex-shrink: 0;
}

.user-info {
  flex: 1;
  min-width: 0;
}

.user-name {
  font-size: 21px;
  line-height: 1.19048;
  font-weight: 600;
  letter-spacing: 0.011em;
  color: #1d1d1f;
  margin-bottom: 2px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-email {
  font-size: 12px;
  line-height: 1.33337;
  color: #86868b;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.user-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #f5f5f7;
}

.user-id {
  font-size: 11px;
  font-weight: 600;
  color: #06c;
  font-family: "SF Mono", Monaco, monospace;
}

.user-badge {
  padding: 2px 8px;
  background: #f0f9ff;
  color: #0071e3;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.hobbies-label {
  font-size: 11px;
  font-weight: 600;
  color: #86868b;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.05em;
}

.hobby-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.hobby-tag {
  padding: 4px 10px;
  background: #f5f5f7;
  border-radius: 6px;
  font-size: 12px;
  color: #1d1d1f;
  transition: background 0.2s;
}

.hobby-tag:hover {
  background: #e8e8ed;
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(10px);
  z-index: 10000;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
}

.modal {
  background: #fff;
  border-radius: 18px;
  width: 100%;
  max-width: 520px;
  max-height: 90vh;
  overflow: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
  padding: 24px 24px 16px;
  border-bottom: 1px solid #d2d2d7;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-title {
  font-size: 24px;
  font-weight: 600;
  color: #1d1d1f;
  letter-spacing: 0.007em;
}

.modal-close {
  width: 30px;
  height: 30px;
  background: #f5f5f7;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #e8e8ed;
}

.modal-close svg {
  width: 14px;
  height: 14px;
}

.modal-body {
  padding: 24px;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #1d1d1f;
  margin-bottom: 8px;
}

.form-input {
  width: 100%;
  padding: 12px 14px;
  background: #fff;
  border: 1px solid #d2d2d7;
  border-radius: 8px;
  font-size: 15px;
  color: #1d1d1f;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #0071e3;
  box-shadow: 0 0 0 4px rgba(0, 113, 227, 0.1);
}

.form-hint {
  font-size: 12px;
  color: #86868b;
  margin-top: 6px;
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 16px 24px 24px;
}

.btn-cancel {
  flex: 1;
  padding: 12px;
  background: #f5f5f7;
  color: #1d1d1f;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel:hover {
  background: #e8e8ed;
}

.btn-submit {
  flex: 1;
  padding: 12px;
  background: #0071e3;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-submit:hover {
  background: #0077ed;
}

/* Side Panel */
.panel-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.3);
  backdrop-filter: blur(10px);
  z-index: 11000;
}

.side-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 440px;
  height: 100vh;
  background: #fff;
  z-index: 11001;
  overflow-y: auto;
  box-shadow: -10px 0 30px rgba(0, 0, 0, 0.1);
}

.panel-header {
  padding: 20px 28px;
  border-bottom: 1px solid #d2d2d7;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  background: #fff;
}

.panel-title {
  font-size: 20px;
  font-weight: 600;
}

.panel-close {
  width: 30px;
  height: 30px;
  background: #f5f5f7;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.panel-close:hover {
  background: #e8e8ed;
}

.panel-body {
  padding: 24px;
}

.panel-user-header {
  text-align: center;
  margin-bottom: 24px;
}

.panel-avatar {
  width: 72px;
  height: 72px;
  margin: 0 auto 16px;
  border-radius: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  font-weight: 600;
  color: #fff;
}

.panel-user-name {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 4px;
}

.panel-user-email {
  font-size: 13px;
  color: #86868b;
}

.info-section {
  margin-bottom: 24px;
}

.info-section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.info-list {
  display: grid;
  gap: 12px;
}

.info-item {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
}

.info-label {
  color: #86868b;
}

.info-value {
  font-weight: 500;
}

.panel-actions {
  display: flex;
  gap: 12px;
}

.panel-btn {
  flex: 1;
  padding: 12px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: all 0.2s;
}

.btn-submit:disabled,
.panel-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.panel-btn-edit {
  background: #f5f5f7;
}

.panel-btn-edit:hover {
  background: #e8e8ed;
}

.panel-btn-delete {
  background: #ffe2e1;
  color: #c53030;
}

.panel-btn-delete:hover {
  background: #ffd1d0;
}

/* Toast */
.toast {
  position: fixed;
  bottom: 24px;
  right: 24px;
  background: #1d1d1f;
  color: #fff;
  padding: 16px 22px;
  border-radius: 12px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  font-size: 15px;
  z-index: 12000;
}

/* Transitions */
.modal-enter-active,
.modal-leave-active,
.panel-enter-active,
.panel-leave-active,
.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.25s ease, transform 0.25s ease;
}

.modal-enter-from,
.modal-leave-to,
.panel-enter-from,
.panel-leave-to,
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateY(12px);
}

@media (max-width: 768px) {
  .hero-headline {
    font-size: 42px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .user-grid {
    grid-template-columns: 1fr;
  }

  .side-panel {
    width: 100%;
  }
}
</style>
