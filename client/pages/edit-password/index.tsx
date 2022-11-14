/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { NextPage } from 'next';
import { Header } from '../../components/common/Header';

const EditPassword: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto">
        <h1>Edit-Password</h1>
      </main>
    </>
  );
};

export default EditPassword;
