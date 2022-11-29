type ProfileImageProps = {
  src: string;
};

import Image from 'next/image';

export const ProfileImage = ({ src }: ProfileImageProps) => {
  // 응답 데이터가 null인 경우 보여질 임시 이미지...!
  if (!src)
    src =
      'https://images.unsplash.com/photo-1665168667719-aad400bd8c4a?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=764&q=80';
  return (
    <div>
      <Image src={src} width="40px" height="40px" className="rounded-full" />
    </div>
  );
};
