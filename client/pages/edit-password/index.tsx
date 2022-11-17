/*
 * 책임 작성자: 정하승
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-14
 */

import { NextPage } from 'next';
import { Header } from '../../components/common/Header';
import { EditPasswordForm } from '../../components/haseung/EditPasswordForm';

const EditPassword: NextPage = () => {
  return (
    <>
      <Header />
      <main className="max-w-[1280px] mx-auto">
        <article className="text-center mt-10">
          <h3 className="font-bold">비밀번호 변경하기</h3>
          <h3 className="mt-5">새로운 비밀번호로 변경합니다.</h3>
          <section className="flex justify-center text-lg">
            <EditPasswordForm />
          </section>
        </article>
      </main>
    </>
  );
};

export default EditPassword;
