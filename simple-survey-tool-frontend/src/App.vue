<template>
  <div class="app">
    <h1>{{ survey.description }}</h1>
    <div v-if="loading">Loading questions...</div>
    <form v-else @submit.prevent="submitAnswers">
      <div
        v-for="(question, index) in questions"
        :key="question.id"
        class="question-block"
      >
        <p>{{ index + 1 }}. {{ question.text }}</p>

        <div class="likert-scale">
          <label
            v-for="(option, idx) in question.responseOptions"
            :key="idx"
            class="likert-option"
          >
            <input
              type="radio"
              :name="'question_' + question.id"
              :value="idx + 1"
              v-model.number="answers[question.id]"
              required
            />
            <span class="circle" />
            <span class="label">{{ option }}</span>
            <span class="circle" />
            <span class="label">{{ label }}</span>
          </label>
        </div>
      </div>

      <button type="submit">Submit Answers</button>

      <div v-if="submitSuccess" class="success-msg">✅ Submitted successfully!</div>
      <div v-if="submitError" class="error-msg">❌ Submission failed. Try again.</div>
    </form>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
// import Question from './QuestionItem.vue';
import { useRoute } from 'vue-router'
import axios from 'axios'


const survey = ref({});
const questions = ref([]);
const loading = ref(true)
const answers = ref({});
onMounted(async() => {
  try {
    const route = useRoute();
    const id = route.params.id 
    console.log(`Fetching survey with id: ${id}`);
    const response = await axios.get(`http://localhost:8080/api/surveys/${id}`);
    loading.value = false;
    console.log(response)
    if (!response.state==200){
      throw new Error(response.statusText);
    } 
    
    let data;
    try {
      data =  response.data;
    } catch (error) {
      console.error("Failed to parse JSON:", error);
      return; // or you can handle this case as needed
    }
  
    survey.value = data;
    questions.value = data.questions;
    console.log(data)
  } catch (error) {
    console.error(error);
  }
})

</script>

<style scoped>
.app {
  max-width: 700px;
  margin: auto;
  padding: 2rem;
  font-family: Arial, sans-serif;
}

.question-block {
  margin-bottom: 2rem;
}

.likert-scale {
  display: flex;
  justify-content: space-between;
  margin-top: 1rem;
  gap: 10px;
}

.likert-option {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

input[type="radio"] {
  margin-bottom: 4px;
}

.label {
  font-size: 0.9rem;
  text-align: center;
  max-width: 90px;
}

button {
  padding: 0.6rem 1.2rem;
  font-size: 1rem;
  margin-top: 1rem;
  cursor: pointer;
}

.success-msg {
  color: green;
  margin-top: 1rem;
}

.error-msg {
  color: red;
  margin-top: 1rem;
}
</style>