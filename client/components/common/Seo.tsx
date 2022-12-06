/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-12-06
 * 최근 수정일: 2022-12-06
 */

import Head from 'next/head';

export const Seo = ({ title }: { title: string }) => {
  return (
    <Head>
      <title>{title}</title>
    </Head>
  );
};
