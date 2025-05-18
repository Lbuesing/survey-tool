import { createApp} from 'vue'
import App from './App.vue'
import router from './router/index.js'

const app = createApp(App)
app.use(router)

// Replace -> app.mount('#app')
router.isReady().then(() => {
    app.mount('#app')
})
// import SurveyForm from './components/SurveyForm.vue';
// app.component(
//     'survey-form',SurveyForm
// )

