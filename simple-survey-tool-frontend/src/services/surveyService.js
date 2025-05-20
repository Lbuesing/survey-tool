import axios from 'axios';

const API_URL = `${process.env.VUE_APP_BACKEND_URL}/api/surveys`;// || 'http://localhost:8080/api/surveys';

export const surveyService = {
  async getSurveyResults(surveyId) {
    try {
      const response = await axios.get(`${API_URL}/${surveyId}/results`);
      return response.data;
    } catch (error) {
      console.error("Error fetching survey results:", error);
      throw error;
    }
  }
};

// Function to fetch the survey by its ID
export const fetchSurveyById = async (surveyId) => {
  const response = await axios.get(`${API_URL}/${surveyId}`);
  if (response.status === 200) {
    return response.data; // return the survey data
  } else {
    throw new Error('Failed to fetch survey');
  }
};

// Function to submit answers to the survey
export const submitAnswersToSurvey = async (surveyId, answers) => {
  try {
    const response = await axios.post(
      `${API_URL}/submit`,
      { surveyId, answers },
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
    return response.data;
  } catch (err) {
    console.error('Error submitting answers:', err);
    throw err;
  }
};