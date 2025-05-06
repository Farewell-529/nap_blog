
export default defineNuxtConfig({
  app: {
    pageTransition: { name: 'page', mode: 'out-in' },
    head: {
      charset: 'utf-8',
      script: [
        {
          hid: 'theme-script',
          innerHTML: `
            const theme = localStorage.getItem('theme') || 'light';
            document.documentElement.setAttribute('data-theme', theme);
            if (theme === 'dark') {
              document.documentElement.classList.add('dark');
              document.documentElement.setAttribute('data-theme', 'dark');
            } else {
              document.documentElement.classList.remove('dark');
              document.documentElement.setAttribute('data-theme', 'light');
            }
          `,
          type: 'text/javascript',
          tagPosition: 'head',
          id: 'theme-script'
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