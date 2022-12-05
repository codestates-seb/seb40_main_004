import { NextPage } from 'next';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { useRecoilValue } from 'recoil';
import { isLoginAtom } from '../../atomsYW';
import { Editor } from '../../components/common/Editor';

const Ask: NextPage = () => {
  const isLogin = useRecoilValue(isLoginAtom);
  const router = useRouter();
  useEffect(() => {
    if (!isLogin) router.back();
  }, []);
  return <Editor />;
};

export default Ask;
