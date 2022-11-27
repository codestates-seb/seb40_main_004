/** @type {import('next').NextConfig} */

// const BASE_URL = 'https://b3a0-39-116-11-157.jp.ngrok.io';
const BASE_URL = 'http://13.125.113.82:8080';

const withPlugins = require('next-compose-plugins');
const nextConfig = {
  reactStrictMode: true,
  swcMinify: true,
  images: {
    domains: [
      'images.unsplash.com',
      'morakmorak.s3.ap-northeast-2.amazonaws.com',
    ],
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
      {
        source: '/api/:path*',
        destination: BASE_URL + '/:path*',
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
