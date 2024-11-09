
export default defineNuxtConfig({
  app: {
    pageTransition: { name: 'page', mode: 'out-in' },
    
    head: {
      charset: 'utf-8',
      script: [
        {
          src:  '/js/initTheme.js'
         }
      ],
    },
  },

  devtools: { enabled: true },
  css: ['~/assets/css/main.css'],

  postcss: {
    plugins: {
      tailwindcss: {},
      autoprefixer: {},
    },
  },
  build: {
    transpile: ['vuetify', 'vue-sonner'],
  },

  modules: [
    '@pinia/nuxt',
    '@pinia-plugin-persistedstate/nuxt',
  ],
  runtimeConfig: {
    public: {
      baseURL: 'http://localhost:8080',
    }
  },

  compatibilityDate: '2024-11-04'
})