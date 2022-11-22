/** @type {import('next').NextConfig} */

const withPlugins = require('next-compose-plugins');
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: ['images.unsplash.com'],
  },
};

const withTM = require('next-transpile-modules')([
  '@fullcalendar/common',
  '@babel/preset-react',
  '@fullcalendar/common',
  '@babel/preset-react',
  '@fullcalendar/daygrid',
  '@fullcalendar/react',
]);

module.exports = withPlugins([withTM], nextConfig);

/* proxy 설정 */
module.exports = {
  async rewrites() {
    return [
      {
        source: `/api/auth`,
        destination: 'https://0371-39-116-11-157.jp.ngrok.io/auth/',
      },
      {
        source: `/api/auth/mail`,
        destination: 'https://0371-39-116-11-157.jp.ngrok.io/auth/mail',
      },
      {
        source: `/api/auth/token`,
        destination: 'https://0371-39-116-11-157.jp.ngrok.io/auth/token',
      },
    ];
  },
};
