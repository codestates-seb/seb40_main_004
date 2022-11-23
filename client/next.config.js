/** @type {import('next').NextConfig} */

const BASE_URL = 'https://b3a0-39-116-11-157.jp.ngrok.io';

const withPlugins = require('next-compose-plugins');
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: ['images.unsplash.com'],
  },
  async rewrites() {
    return [
      {
        source: `/api/auth`,
        destination: BASE_URL + '/auth/',
      },
      {
        source: `/api/auth/mail`,
        destination: BASE_URL + '/auth/mail',
      },
      {
        source: `/api/auth/token`,
        destination: BASE_URL + '/auth/token',
      },
    ];
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
