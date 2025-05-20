<template>
  <div class="not-found">
    <h1>404 - Page Not Found</h1>
    <p>The page you’re looking for doesn’t exist.</p>
    <router-link to="/surveys/some-valid-id">Go to a valid survey</router-link>
  </div>
</template>

<script setup>
</script>

<style scoped>
.not-found {
  text-align: center;
  padding: 3rem;
}
</style>