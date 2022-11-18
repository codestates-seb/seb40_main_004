/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-18
 */

import { faComment } from '@fortawesome/free-regular-svg-icons';
import { faHeart } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Link from 'next/link';

export const ListLately = () => {
  return (
    <>
      <div className="mb-6">
        <span className="text-2xl mr-6 font-bold">❓ 최근 질문</span>
        <Link href="/questions">
          <span className="text-xs hover:cursor-pointer">더보기 ＞</span>
        </Link>
      </div>
      <div className="flex justify-between">
        <div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
          <div className="w-[492px] h-16 border-b mb-8">
            <div className="mb-4">
              <span className="text-lg font-bold">
                타입스크립트 질문드립니다!!
              </span>
            </div>
            <div className="flex justify-between items-center">
              <div className="flex gap-6">
                <span className="text-xs">김코딩</span>
                <span className="text-xs">1분 전</span>
              </div>
              <div className="flex gap-4">
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faHeart} size="xs" />
                  <span className="text-xs">13</span>
                </div>
                <div className="flex gap-2">
                  <FontAwesomeIcon icon={faComment} size="xs" />
                  <span className="text-xs">1</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};
