/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-20
 * 최근 수정일: 2022-11-20
 */

import Image from 'next/image';
import { useState } from 'react';

export const EditAvatar = () => {
  const [isClicked, setIsClicked] = useState(false);
  console.log(isClicked);
  return (
    <>
      <div className="relative group">
        <Image src="/favicon.ico" width="238px" height="238px" />
        <button
          className="py-[6px] px-2 absolute left-4 bottom-4 bg-background-gray rounded-full"
          onClick={() => setIsClicked((prev) => !prev)}
        >
          ✏️ 편집
        </button>
      </div>
      {isClicked ? (
        <div className="relative -left-8 bottom-8">
          <ul className="border border-solid border-black border-opacity-10 border-spacing-1 right-0 w-[200px] rounded-xl absolute top-8 bg-background-gray z-20">
            <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer mt-2 py-1 px-4 rounded-xl text-[15px]">
              <span className="ml-2">업데이트</span>
            </li>
            <li className="hover:bg-main-yellow hover:bg-opacity-40 hover:cursor-pointer py-1 mb-2 px-4 rounded-xl text-[15px]">
              <span className="ml-2">삭제</span>
            </li>
          </ul>
        </div>
      ) : null}
    </>
  );
};
