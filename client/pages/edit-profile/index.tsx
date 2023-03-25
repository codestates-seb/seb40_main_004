import { GetServerSideProps, NextPage } from 'next';
import { useRouter } from 'next/router';
import { useEffect } from 'react';

import { Footer } from '@components/common/Footer';
import { Header } from '@components/common/Header';
import { Seo } from '@components/common/Seo';
import { AsideEditProfile } from '@components/edit-privacy/AsideEditProfile';

import { EditProfileComponent } from '@components/edit-privacy/EditProfile';
import { toast } from 'react-toastify';
import { EditAvatar } from '@components/edit-privacy/EditAvatar';

const EditProfile: NextPage = () => {
  const router = useRouter();
  useEffect(() => {
    if (typeof window !== 'undefined') {
      if (!localStorage.getItem('refreshToken')) {
        toast.error('로그인이 필요한 페이지입니다');
        router.push('/');
      }
    }
  }, []);
  return (
    <>
      <Seo title="개인정보 수정 - 프로필" />
      <Header />
      <main className="w-[1280px] min-h-screen mx-auto flex justify-between mb-12">
        <div className="w-[280px] pt-12">
          <AsideEditProfile />
        </div>
        <div className="w-2/4">
          <EditProfileComponent />
        </div>
        <div className="pt-12 w-1/5">
          <EditAvatar />
        </div>
      </main>
      <Footer />
    </>
  );
};

export const getServerSideProps: GetServerSideProps = async (context) => {
  const content = context.req.url?.split('/')[1];
  return {
    props: {
      content,
    },
  };
};

export default EditProfile;
