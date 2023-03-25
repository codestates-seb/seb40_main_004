import { userEmailAtom } from '@atoms/userAtom';
import { AuthResp } from '@type/login';

import { useRouter } from 'next/router';
import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useSetRecoilState } from 'recoil';

export const useCheckAuth = () => {
  const { register, handleSubmit } = useForm<AuthResp>({ mode: 'onChange' });
  const [isSubmitting, setIsSubmitting] = useState(false);
  const setEmail = useSetRecoilState(userEmailAtom);
  const router = useRouter();
  return {
    register,
    handleSubmit,
    isSubmitting,
    setIsSubmitting,
    setEmail,
    router,
  };
};
