<template>
  <div class="app">
    <h1>{{ survey.description }}</h1>

    <!-- Loading and error messages -->
    <div v-if="loading">Loading questions...</div>
    <div v-if="surveyError" class="error-msg">
      ❌ Failed to load survey: {{ surveyError }}
    </div>

    <!-- Form for submitting answers, visible only if submission is not successful and survey is loaded -->
    <form v-if="surveyLoaded && !submitSuccess" @submit.prevent="submitAnswers">
      <div
        v-for="(question, index) in questions"
        :key="question.id"
        class="question-block"
      >
        <!-- Display the question text -->
        <p>{{ index + 1 }}. {{ question.text }}</p>

        <!-- Likert scale options -->
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
          </label>
        </div>
      </div>

      <!-- Submit button, disabled if answers are incomplete or submission is in progress -->
      <button type="submit" :disabled="isSubmitDisabled || submitSuccess">
        Submit Answers
      </button>

      <!-- Display error message if submission fails -->
      <div v-if="submitError" class="error-msg">
        ❌ Submission failed. Try again.
      </div>
    </form>

    <!-- Success message after successful submission -->
    <div v-if="submitSuccess">
      <p>✅ Thank you for submitting your answers!</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { fetchSurveyById, submitAnswersToSurvey } from '@/services/surveyService';

// Declare reactive properties
const survey = ref({});
const questions = ref([]);
const answers = ref({});
const loading = ref(true);
const surveyError = ref(null);
const submitSuccess = ref(false);
const submitError = ref(false);
const surveyLoaded = ref(false); // Flag to track if the survey has been successfully loaded

// Fetch survey and questions on component mount
onMounted(async () => {
  const route = useRoute();
  const surveyId = route.params.id;

  if (!surveyId) {
    surveyError.value = "The page you are looking for does not exist";
    loading.value = false;
    return;
  }

  try {
    const data = await fetchSurveyById(surveyId);
    survey.value = data;
    questions.value = data.questions;
    surveyLoaded.value = true; // Set surveyLoaded to true once the survey is successfully fetched
    document.title = 'Survey';
  } catch (error) {
    handleSurveyError(error);
  } finally {
    loading.value = false;
  }
});

// Handle survey loading errors
function handleSurveyError(error) {
  if (error.response) {
    const status = error.response.status;
    surveyError.value =
      status === 400
        ? "Invalid survey ID format."
        : status === 404
        ? "Survey not found."
        : "An unexpected error occurred.";
  } else {
    surveyError.value = "Could not connect to the server.";
  }

  console.error("Survey fetch error:", error);
}

// Computed property to check if all answers are filled
const isSubmitDisabled = computed(() => {
  return Object.keys(answers.value).length !== questions.value.length;
});

// Submit answers to the server
const submitAnswers = async () => {
  try {
    submitSuccess.value = false;
    submitError.value = false;

    // Prepare the payload
    const payload = {
      surveyId: survey.value.id,
      answers: answers.value,
    };

    // Call the service function to submit answers, passing the payload
    await submitAnswersToSurvey(payload.surveyId, payload.answers);

    submitSuccess.value = true;
  } catch (error) {
    submitError.value = true;
    console.error("Error submitting answers:", error);
  }
};
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
