// import this after install `@mdi/font` package
import '@mdi/font/css/materialdesignicons.css'

import 'vuetify/styles'
import { createVuetify } from 'vuetify'
import { zhHans,en } from 'vuetify/locale'
import { VDateInput } from 'vuetify/labs/VDateInput'
export default defineNuxtPlugin((app) => {
  const vuetify = createVuetify({
    locale: {
      locale: 'zhHans',
      fallback: 'en ',
      messages: { zhHans, en },
    },
    components: {
      VDateInput,
    }
  })
  app.vueApp.use(vuetify)
})
