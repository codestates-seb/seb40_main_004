import { GetServerSideProps, NextPage } from 'next';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { toast } from 'react-toastify';
import { useRecoilValue } from 'recoil';

import { isLoginAtom } from '@atoms/loginAtom';

import { Editor } from '@components/common/QuillEditor/Editor';
import { Seo } from '@components/common/Seo';

const Ask: NextPage = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  const router = useRouter();
  useEffect(() => {
    if (!isLogin) {
      toast.error('로그인 해주세요.');
      router.back();
    }
  }, []);
  return (
    <>
      <Seo title="질문 작성" />
      <Editor />
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

export default Ask;
