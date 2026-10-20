<script setup lang="ts">
import { ref } from 'vue';
import { api, type Loan, type Problem } from './api/client';

const memberId = ref(1);
const bookId = ref(1);
const loans = ref<Loan[]>([]);
const message = ref('');

async function load() {
  message.value = '';
  const { data, error } = await api.GET('/members/{memberId}/loans', {
    params: { path: { memberId: memberId.value } },
  });
  loans.value = data ?? [];
  show(error);
}

async function lend() {
  const { error } = await api.POST('/loans', {
    body: { memberId: memberId.value, bookId: bookId.value },
  });
  await load();
  show(error);
}

async function giveBack(loanId: string) {
  const { error } = await api.POST('/loans/{loanId}/return', {
    params: { path: { loanId } },
  });
  await load();
  show(error);
}

function show(error?: Problem) {
  if (error) message.value = error.detail ?? 'Something went wrong';
}
</script>

<template>
  <main>
    <h1>Front desk</h1>

    <p>
      Member <input v-model.number="memberId" type="number" min="1" />
      <button @click="load">Show loans</button>
    </p>

    <p>
      Book <input v-model.number="bookId" type="number" min="1" />
      <button @click="lend">Lend</button>
    </p>

    <p v-if="message" class="error">{{ message }}</p>

    <table v-if="loans.length">
      <tr><th>Book</th><th>Borrowed at</th><th></th></tr>
      <tr v-for="loan in loans" :key="loan.id">
        <td>{{ loan.bookId }}</td>
        <td>{{ new Date(loan.borrowedAt).toLocaleString() }}</td>
        <td><button @click="giveBack(loan.id)">Return</button></td>
      </tr>
    </table>
    <p v-else>No active loans.</p>
  </main>
</template>

<style>
body { font-family: system-ui, sans-serif; margin: 2rem; }
input { width: 4rem; }
td, th { padding: 0.25rem 1rem 0.25rem 0; text-align: left; }
.error { color: #b0322f; }
</style>
