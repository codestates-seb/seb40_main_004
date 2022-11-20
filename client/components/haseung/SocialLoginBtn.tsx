import { faGithub } from '@fortawesome/free-brands-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import KaKao from '../../public/kakao.png';

export const SocialLoginBtn = () => {
  const handleKaKaoClick = () => {
    // console.log('clicked');
  };
  return (
    <section className="flex justify-around w-full">
      <button className="bg-main-gray rounded-full">
        <FontAwesomeIcon
          icon={faGithub}
          className="cursor-pointer w-44 h-8 mr-3 mx-2"
        />
      </button>
      <Image
        onClick={handleKaKaoClick}
        src={KaKao}
        alt="kakaologin"
        className="cursor-pointer rounded-full"
        priority
      />
    </section>
  );
};
