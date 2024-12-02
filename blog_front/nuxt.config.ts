
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
      BLOG_OWNER_EMAILS:'2922349007@qq.com',
      BLOG_OWNER_NAME:'Nap'
    }
  },

  compatibilityDate: '2024-11-04'
})