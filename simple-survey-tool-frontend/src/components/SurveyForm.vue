<template>
  <div class="survey-form">
    <!-- Display survey title -->
    <h1>{{ survey.title }}</h1>

    <!-- Loop through questions and display them in a form -->
    <form @submit.prevent="handleSubmit()">
      <ul v-for="(question, index) of questions" :key="index">
        <li>
          {{ question.text }}
          <input
            type="text"
            v-model.trim="answers[index]"
            placeholder="Answer..."
          />
        </li>
      </ul>

      <!-- Submit button -->
      <button type="submit">Submit</button>
    </form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      answers: [], // store user's answers
    };
  },
  computed: {
    survey() {
      return this.$store.state.survey;
    },
    questions() {
      return this.$store.state.questions;
    },
  },
  methods: {
    handleSubmit() {
      const answers = JSON.stringify(this.answers);
      console.log("Answers:", answers);

      // Send the answers to your API (e.g., http://localhost:8080/api/surveys/submit)
      fetch(`http://localhost:8080/api/surveys/${this.survey.id}/submit`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: answers,
      })
        .then((response) => response.json())
        .then((data) => console.log("Survey submitted:", data))
        .catch((error) => console.error(error));
    },
  },
};
</script>