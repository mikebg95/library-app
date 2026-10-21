<script setup lang="ts">
import { onMounted, ref } from 'vue';
import { api, type Loan, type Problem } from './api/client';

const memberId = ref(1);
const bookId = ref(1);
const loans = ref<Loan[]>([]);
const notice = ref<{ kind: 'success' | 'error'; text: string } | null>(null);

async function load() {
  notice.value = null;
  const { data, error } = await api.GET('/members/{memberId}/loans', {
    params: { path: { memberId: memberId.value } },
  });
  loans.value = data ?? [];
  show(error);
}

async function lend() {
  const { data, error } = await api.POST('/loans', {
    body: { memberId: memberId.value, bookId: bookId.value },
  });
  await load();
  show(error, data && `Book ${data.bookId} lent to member ${data.memberId}.`);
}

async function giveBack(loanId: string) {
  const { data, error } = await api.POST('/loans/{loanId}/return', {
    params: { path: { loanId } },
  });
  await load();
  show(error, data && `Book ${data.bookId} returned.`);
}

function show(error?: Problem, success?: string) {
  if (error) notice.value = { kind: 'error', text: error.detail ?? 'Something went wrong' };
  else if (success) notice.value = { kind: 'success', text: success };
}

function formatDate(value: string) {
  return new Date(value).toLocaleString(undefined, { dateStyle: 'medium', timeStyle: 'short' });
}

onMounted(load);
</script>

<template>
  <main class="card">
    <header>
      <p class="eyebrow">Library</p>
      <h1>Front desk</h1>
    </header>

    <div class="forms">
      <form @submit.prevent="load">
        <label for="member">Member</label>
        <div class="field">
          <input id="member" v-model.number="memberId" type="number" min="1" />
          <button type="submit" class="secondary">Show loans</button>
        </div>
      </form>

      <form @submit.prevent="lend">
        <label for="book">Book</label>
        <div class="field">
          <input id="book" v-model.number="bookId" type="number" min="1" />
          <button type="submit">Lend</button>
        </div>
      </form>
    </div>

    <p v-if="notice" :class="['notice', notice.kind]" role="status">{{ notice.text }}</p>

    <section>
      <h2>
        Active loans
        <span class="count">{{ loans.length }}</span>
      </h2>

      <ul v-if="loans.length" class="loans">
        <li v-for="loan in loans" :key="loan.id">
          <div>
            <p class="book">Book {{ loan.bookId }}</p>
            <p class="date">Borrowed {{ formatDate(loan.borrowedAt) }}</p>
          </div>
          <button type="button" class="ghost" @click="giveBack(loan.id)">Return</button>
        </li>
      </ul>
      <p v-else-if="notice?.kind !== 'error'" class="empty">No active loans for member {{ memberId }}.</p>
    </section>

    <details class="hint">
      <summary>Try it</summary>
      <ul>
        <li>Seed data: members 1–3, books 1–5.</li>
        <li>A member can have at most 3 active loans.</li>
        <li>A book can be on loan to one member at a time.</li>
        <li>Unknown members or books, and empty fields, show the error from the API.</li>
      </ul>
    </details>
  </main>
</template>

<style>
:root {
  --bg: #f5f5f4;
  --card: #ffffff;
  --ink: #1c1917;
  --muted: #78716c;
  --line: #e7e5e4;
  --accent: #1c1917;
  --accent-ink: #ffffff;
  --error: #b42318;
  --error-bg: #fef3f2;
  --success: #067647;
  --success-bg: #ecfdf3;
  color-scheme: light;
}

@media (prefers-color-scheme: dark) {
  :root {
    --bg: #0c0a09;
    --card: #1c1917;
    --ink: #f5f5f4;
    --muted: #a8a29e;
    --line: #292524;
    --accent: #f5f5f4;
    --accent-ink: #1c1917;
    --error: #fda29b;
    --error-bg: #2a1614;
    --success: #75e0a7;
    --success-bg: #0f2419;
    color-scheme: dark;
  }
}

* { box-sizing: border-box; }

body {
  margin: 0;
  padding: 10vh 16px 48px;
  background: var(--bg);
  color: var(--ink);
  font: 15px/1.5 ui-sans-serif, system-ui, -apple-system, 'Segoe UI', sans-serif;
  -webkit-font-smoothing: antialiased;
}

p, h1, h2 { margin: 0; }

.card {
  max-width: 520px;
  margin: 0 auto;
  background: var(--card);
  border: 1px solid var(--line);
  border-radius: 16px;
  padding: 32px;
  display: grid;
  gap: 28px;
  box-shadow: 0 1px 2px rgb(0 0 0 / 0.04), 0 8px 24px rgb(0 0 0 / 0.04);
}

.eyebrow {
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: var(--muted);
}

h1 { font-size: 28px; font-weight: 650; letter-spacing: -0.02em; }

h2 {
  font-size: 13px;
  font-weight: 600;
  color: var(--muted);
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.forms { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }

label { display: block; font-size: 13px; font-weight: 500; color: var(--muted); margin-bottom: 6px; }

.field { display: flex; gap: 8px; }

input {
  width: 72px;
  flex: none;
  font: inherit;
  color: inherit;
  background: transparent;
  border: 1px solid var(--line);
  border-radius: 10px;
  padding: 8px 10px;
}

input:focus-visible, button:focus-visible { outline: 2px solid var(--accent); outline-offset: 2px; }

button {
  flex: 1;
  font: inherit;
  font-weight: 550;
  cursor: pointer;
  border-radius: 10px;
  padding: 8px 14px;
  border: 1px solid var(--accent);
  background: var(--accent);
  color: var(--accent-ink);
  transition: opacity 0.15s;
}

button:hover { opacity: 0.85; }
button.secondary, button.ghost { background: transparent; color: var(--ink); border-color: var(--line); }
button.secondary:hover, button.ghost:hover { opacity: 1; border-color: var(--muted); }
button.ghost { flex: none; font-size: 13px; padding: 6px 12px; }

.notice { border-radius: 10px; padding: 10px 14px; font-size: 14px; }
.notice.error { background: var(--error-bg); color: var(--error); }
.notice.success { background: var(--success-bg); color: var(--success); }

.hint { font-size: 13px; color: var(--muted); }
.hint summary { cursor: pointer; font-weight: 550; width: fit-content; }
.hint ul { margin: 8px 0 0; padding-left: 18px; display: grid; gap: 4px; }

.count {
  font-size: 12px;
  font-variant-numeric: tabular-nums;
  background: var(--bg);
  border: 1px solid var(--line);
  border-radius: 999px;
  padding: 0 8px;
}

.loans { list-style: none; margin: 0; padding: 0; border-top: 1px solid var(--line); }

.loans li {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 0;
  border-bottom: 1px solid var(--line);
}

.book { font-weight: 550; }
.date, .empty { font-size: 13px; color: var(--muted); }
.empty { padding: 14px 0; border-top: 1px solid var(--line); }

@media (max-width: 480px) {
  .card { padding: 24px 20px; }
  .forms { grid-template-columns: 1fr; }
}
</style>
