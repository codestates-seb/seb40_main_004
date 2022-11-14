/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { NextPage } from 'next';
import { Header } from '../../components/common/Header';

const EditProfile: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto">
        <h1>Edit-Profile</h1>
      </main>
    </>
  );
};

export default EditProfile;
