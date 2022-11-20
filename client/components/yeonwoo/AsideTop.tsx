/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

import { faBloggerB, faGithub } from '@fortawesome/free-brands-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Image from 'next/image';
import { Button } from '../common/Button';

export const AsideTop = () => {
  return (
    <>
      <Image src="/favicon.ico" width="238px" height="238px" />
      <div className="mt-2">
        <div className="flex justify-between items-baseline">
          <span className="text-3xl font-bold">김코딩</span>
          <Button>후원</Button>
        </div>
        <div className="-mt-1">
          <span className="text-sm">yeonwoopark22@gmail.com</span>
        </div>
        <div className="my-2">
          <span className="text-xl font-medium">추운 겨울이지만 화이팅</span>
        </div>
        <div className="flex justify-between items-start">
          <div>
            <span className="text-xl text-main-orange font-semibold">
              260 모락
            </span>
          </div>
          <div className="flex gap-4 text-xl">
            <span className="text-2xl">💎</span>
            <span># 3</span>
          </div>
        </div>
        <div className="mt-2 mb-4 flex gap-8">
          <FontAwesomeIcon icon={faGithub} size="2xl" />
          <FontAwesomeIcon icon={faBloggerB} size="2xl" />
        </div>
        <div>
          <button className="bg-main-yellow py-[6px] rounded-full w-full">
            프로필 수정
          </button>
        </div>
      </div>
    </>
  );
};
