type ProfileImageProps = {
  src: string;
};

import Image from 'next/image';

export const ProfileImage = ({ src }: ProfileImageProps) => {
  return (
    <div>
      <Image src={src} width="40px" height="40px" className="rounded-full" />
    </div>
  );
};
