<template>
  <div>
    <header class="card" style="margin-bottom: 24px;">
      <h1 style="margin: 0; font-size: 1.8rem; font-weight: 700;">SK085 Spring Boot Practice</h1>
      <p style="margin: 6px 0 0; color: #475569;">백엔드 API와 통신하는 사용자 관리 대시보드</p>
    </header>

    <div class="grid two" style="margin-bottom: 24px;">
      <section class="card">
        <h2 style="margin-top: 0;">개발자 / 팀 정보</h2>
        <div v-if="developer" class="grid">
          <div>
            <h3 style="margin-bottom: 4px; font-size: 1rem; color: #1f2937;">Owner</h3>
            <p style="margin: 0;">{{ developer.owner.name }}</p>
            <small style="color: #64748b;">{{ developer.owner.role }} • {{ developer.owner.level }}</small>
          </div>
          <div>
            <h3 style="margin-bottom: 4px; font-size: 1rem; color: #1f2937;">Team</h3>
            <p style="margin: 0;">{{ developer.team.position }}</p>
            <small style="color: #64748b;">{{ developer.team.detail }}</small>
          </div>
        </div>
        <p v-else class="badge warning">정보를 불러오는 중...</p>
      </section>

      <section class="card">
        <h2 style="margin-top: 0;">애플리케이션 상태</h2>
        <div v-if="probe">
          <div class="status-indicator" style="margin-bottom: 12px;">
            <span :class="probeBadgeClass(probe.liveness)"></span>
            <strong>Liveness:</strong>
            <span>{{ probe.liveness }}</span>
          </div>
          <div class="status-indicator" style="margin-bottom: 16px;">
            <span :class="probeBadgeClass(probe.readiness)"></span>
            <strong>Readiness:</strong>
            <span>{{ probe.readiness }}</span>
          </div>
          <div class="form-actions" style="justify-content: flex-start;">
            <button class="secondary" @click="setProbe('CORRECT', 'ACCEPTING_TRAFFIC')" :disabled="probeBusy">
              정상 상태 전환
            </button>
            <button class="danger" @click="setProbe('BROKEN', 'REFUSING_TRAFFIC')" :disabled="probeBusy">
              점검 모드 전환
            </button>
          </div>
        </div>
        <p v-else class="badge warning">상태 정보를 불러오는 중...</p>
      </section>
    </div>

    <section class="card">
      <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
        <div>
          <h2 style="margin: 0 0 4px;">사용자 목록</h2>
          <p style="margin: 0; color: #6b7280;">백엔드의 /api/users 엔드포인트와 연동</p>
        </div>
        <button class="primary" @click="openCreate">+ 사용자 추가</button>
      </div>

      <div style="display: flex; gap: 12px; margin-bottom: 16px;">
        <input
          v-model="filters.name"
          placeholder="이름으로 검색 (선택사항)"
          @keyup.enter="loadUsers"
        />
        <button class="secondary" @click="loadUsers">검색</button>
      </div>

      <div v-if="usersLoading" class="badge warning">사용자 데이터를 불러오는 중...</div>
      <div v-else>
        <table class="table">
          <thead>
            <tr>
              <th>ID</th>
              <th>이름</th>
              <th>이메일</th>
              <th>취미</th>
              <th style="width: 140px;">관리</th>
            </tr>
          </thead>
          <tbody>
            <tr v-if="users.length === 0">
              <td colspan="5" style="text-align: center; color: #64748b;">
                등록된 사용자가 없습니다.
              </td>
            </tr>
            <tr v-for="user in users" :key="user.id">
              <td>{{ user.id }}</td>
              <td>{{ user.name }}</td>
              <td>{{ user.email }}</td>
              <td>
                <span v-for="hobby in user.hobbies" :key="hobby" class="tag">{{ hobby }}</span>
              </td>
              <td>
                <div style="display: flex; gap: 8px;">
                  <button class="secondary" @click="openEdit(user)">수정</button>
                  <button class="danger" @click="confirmDelete(user)" :disabled="deleteBusy">
                    삭제
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <transition name="fade">
      <div v-if="isModalOpen" class="modal-backdrop" @click.self="closeModal">
        <div class="modal">
          <h2 style="margin-top: 0; margin-bottom: 16px;">{{ formTitle }}</h2>
          <form @submit.prevent="submitUser">
            <div class="grid">
              <label>
                <span>이름</span>
                <input v-model.trim="userForm.name" required placeholder="홍길동" />
              </label>
              <label>
                <span>이메일</span>
                <input v-model.trim="userForm.email" required type="email" placeholder="user@example.com" />
              </label>
              <label>
                <span>취미</span>
                <input v-model="userForm.hobbiesText" placeholder="독서, 러닝" />
                <small style="color: #64748b;">쉼표로 구분하여 입력 (최대 10개)</small>
              </label>
            </div>
            <div class="form-actions">
              <button type="button" class="secondary" @click="closeModal">취소</button>
              <button type="submit" class="primary" :disabled="formBusy">
                {{ editingUser ? '수정하기' : '등록하기' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </transition>

    <transition name="fade">
      <div v-if="toast.visible" class="card" style="position: fixed; bottom: 24px; right: 24px; max-width: 320px;">
        <strong>{{ toast.title }}</strong>
        <p style="margin: 6px 0 0;">{{ toast.message }}</p>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { reactive, ref, computed, onMounted } from 'vue';
import {
  fetchDeveloperInfo,
  fetchProbeStatus,
  updateProbeStatus,
  fetchUsers,
  createUser,
  updateUser,
  deleteUser
} from './services/api.js';

const developer = ref(null);
const probe = ref(null);
const users = ref([]);
const usersLoading = ref(false);
const probeBusy = ref(false);
const deleteBusy = ref(false);
const formBusy = ref(false);

const filters = reactive({ name: '' });

const isModalOpen = ref(false);
const editingUser = ref(null);
const userForm = reactive({
  name: '',
  email: '',
  hobbiesText: ''
});

const toast = reactive({ visible: false, title: '', message: '' });

const showToast = (title, message) => {
  toast.visible = true;
  toast.title = title;
  toast.message = message;
  setTimeout(() => {
    toast.visible = false;
  }, 2500);
};

const loadDeveloperInfo = async () => {
  try {
    developer.value = await fetchDeveloperInfo();
  } catch (err) {
    console.error(err);
    showToast('오류', '개발자 정보를 불러오지 못했습니다.');
  }
};

const loadProbe = async () => {
  try {
    probe.value = await fetchProbeStatus();
  } catch (err) {
    console.error(err);
    showToast('오류', '상태 정보를 불러오지 못했습니다.');
  }
};

const loadUsers = async () => {
  usersLoading.value = true;
  try {
    users.value = await fetchUsers(filters.name.trim());
  } catch (err) {
    console.error(err);
    showToast('오류', '사용자 목록을 불러오지 못했습니다.');
  } finally {
    usersLoading.value = false;
  }
};

const setProbe = async (liveness, readiness) => {
  probeBusy.value = true;
  try {
    const updated = await updateProbeStatus({ liveness, readiness });
    probe.value = updated;
    showToast('성공', '애플리케이션 상태가 업데이트되었습니다.');
  } catch (err) {
    console.error(err);
    showToast('오류', '애플리케이션 상태 변경에 실패했습니다.');
  } finally {
    probeBusy.value = false;
  }
};

const resetForm = () => {
  userForm.name = '';
  userForm.email = '';
  userForm.hobbiesText = '';
};

const openCreate = () => {
  editingUser.value = null;
  resetForm();
  isModalOpen.value = true;
};

const openEdit = (user) => {
  editingUser.value = user;
  userForm.name = user.name;
  userForm.email = user.email;
  userForm.hobbiesText = user.hobbies?.join(', ') || '';
  isModalOpen.value = true;
};

const closeModal = () => {
  isModalOpen.value = false;
  editingUser.value = null;
  resetForm();
};

const submitUser = async () => {
  formBusy.value = true;
  const payload = {
    name: userForm.name.trim(),
    email: userForm.email.trim(),
    hobbies: userForm.hobbiesText
      .split(',')
      .map((hobby) => hobby.trim())
      .filter(Boolean)
  };

  try {
    if (editingUser.value) {
      await updateUser(editingUser.value.id, { ...editingUser.value, ...payload });
      showToast('수정 완료', '사용자 정보가 업데이트되었습니다.');
    } else {
      await createUser(payload);
      showToast('등록 완료', '새 사용자가 생성되었습니다.');
    }
    closeModal();
    await loadUsers();
  } catch (err) {
    console.error(err);
    showToast('오류', '사용자 저장에 실패했습니다. 입력값을 확인하세요.');
  } finally {
    formBusy.value = false;
  }
};

const confirmDelete = async (user) => {
  const agree = window.confirm(`정말 ${user.name} 사용자를 삭제할까요?`);
  if (!agree) return;

  deleteBusy.value = true;
  try {
    await deleteUser(user.id);
    showToast('삭제 완료', '사용자가 삭제되었습니다.');
    await loadUsers();
  } catch (err) {
    console.error(err);
    showToast('오류', '사용자 삭제에 실패했습니다.');
  } finally {
    deleteBusy.value = false;
  }
};

const formTitle = computed(() => (editingUser.value ? '사용자 정보 수정' : '새 사용자 추가'));

const probeBadgeClass = (state) => {
  if (!state) return '';
  const normalized = state.toUpperCase();
  if (normalized.includes('ACCEPT') || normalized === 'CORRECT') return 'healthy';
  if (normalized.includes('REFUS')) return 'degraded';
  return 'down';
};

onMounted(async () => {
  await Promise.all([loadDeveloperInfo(), loadProbe(), loadUsers()]);
});
</script>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
