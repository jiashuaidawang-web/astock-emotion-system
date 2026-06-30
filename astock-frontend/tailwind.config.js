/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{vue,ts}'],
  theme: {
    extend: {
      colors: {
        terminal: {
          base: '#0B0E11',
          panel: '#0F141C',
          card: '#181E25',
          line: 'rgba(255,255,255,0.05)',
          main: '#EAECEF',
          sub: '#707A8A',
          up: '#0ECB81',
          down: '#F6465D',
          neutral: '#848E9C'
        }
      }
    }
  },
  plugins: []
}
