/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-14
 * 최근 수정일: 2022-11-20
 */

import { NextPage } from 'next';
import { Footer } from '../../components/common/Footer';
import { Header } from '../../components/common/Header';
import { AsideEditProfile } from '../../components/yeonwoo/AsideEditProfile';
import { EditAvatar } from '../../components/yeonwoo/EditAvatar';
import { EditProfileComponent } from '../../components/yeonwoo/EditProfile';

const EditProfile: NextPage = () => {
  return (
    <>
      <Header />
      <main className="w-[1280px] min-h-screen mx-auto flex justify-between mb-12">
        <div className="w-[280px] pt-12">
          <AsideEditProfile />
        </div>
        <div className="w-2/4">
          <EditProfileComponent />
        </div>
        <div className="pt-12">
          <EditAvatar />
        </div>
      </main>
      <Footer />
    </>
  );
};

export default EditProfile;
