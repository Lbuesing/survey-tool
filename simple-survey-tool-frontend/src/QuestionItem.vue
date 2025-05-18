<template>
  <div class="question-item">
    {{ question.text }}
    <input type="text" name="{{ question.id }}" />
  </div>
</template>

<script setup>
// import { ref } from 'vue';

// function onAnswerChange() {
//   // Update the parent component with the new answer (not needed in this case)
// }
</script>