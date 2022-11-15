import { faGithub } from '@fortawesome/free-brands-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import KaKao from '../../public/kakao.png';

export const SocialLoginBtn = () => {
  return (
    <section className="flex justify-start w-full">
      <button className="bg-main-gray rounded-full mr-3 w-2/4 h-11 mx-2">
        <FontAwesomeIcon icon={faGithub} className="fa-lg cursor-pointer" />
      </button>
      <button>
        <Image src={KaKao} alt="kakaologin" />
      </button>
    </section>
  );
};
