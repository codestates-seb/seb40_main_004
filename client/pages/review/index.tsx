/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-20
 * 개요
   - 답변을 채택할 때 리뷰 태그를 선택할 수 있는 페이지 입니다.
 */

import { NextPage } from 'next';
import Link from 'next/link';
import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { useRecoilState } from 'recoil';
import { selectedTagsAtom } from '../../atomsHJ';
import { ReviewTag } from '../../components/hyejung/ReviewTag';
import { ProgressBar } from '../../components/hyejung/ProgressBar';
import { BtnBackArticle } from '../../components/hyejung/BtnBackArticle';

const tags = [
  '친절한',
  '따듯한',
  '꼼꼼한',
  '똑똑한',
  '사려 깊은',
  '훌륭한',
  '너그러운',
  '열정 있는',
  '현명한',
  '말을 잘 하는',
  '믿음직한',
  '도움이 되는',
  '문제를 해결하는',
  '배려심 있는',
  '재치 있는',
  '창의적인',
  '논리적인',
  '이해하기 쉬운',
];

const ReviewTagContainer = () => {
  // 리뷰 태그를 추가로 선택할 수 있는지 여부를 판단하는 isAble state
  const [isSelectable, setIsSelectable] = useState(true);
  // 선택된 리뷰태그가 저장되는 selectedTags 전역상태
  const [selectedTags, setSelectedTags] = useRecoilState(selectedTagsAtom);
  // selectedTags가 변경될때마다 길이를 확인해 isSelectable 상태를 업데이트

  useEffect(() => {
    if (selectedTags.length === 3) setIsSelectable(false);
    else setIsSelectable(true);
  }, [selectedTags]);

  useEffect(() => {
    setSelectedTags([]);
  }, []);

  return (
    <section className="flex justify-between h-full md:border-l sm:space-x-10">
      <ProgressBar pageNumber={0} />
      <section className="flex flex-col space-y-10 w-full">
        <article className="text-left space-y-2 flex flex-col">
          <h1 className="text-2xl font-bold text-right">
            🔖채택하실 답변을 설명할 수 있는 태그를 골라주세요!
          </h1>
          <span className="font-bold text-right">
            최소 1개, 최대 3개까지 선택하실 수 있어요!
          </span>
        </article>

        <section className="flex w-full p-6 h-min-[350px] bg-white rounded-[20px] justify-center items-center">
          <article className="flex flex-wrap justify-center items-center">
            {tags.map((tag) => (
              <ReviewTag key={tag} isSelectable={isSelectable}>
                {tag}
              </ReviewTag>
            ))}
          </article>
        </section>

        <article className="ml-auto text-right space-x-3">
          {selectedTags.length > 0 ? (
            <Link href={'/review/message'}>
              <button className="text-base sm:text-lg font-bold">
                다음 단계로!
                <FontAwesomeIcon icon={faChevronRight} className="fa-lg mr-1" />
              </button>
            </Link>
          ) : (
            <div className="text-base sm:text-lg font-bold text-main-gray">
              최소 1개 이상 선택해주세요!
            </div>
          )}
        </article>
      </section>
    </section>
  );
};

const Review: NextPage = () => {
  return (
    <>
      <main className="max-w-[1280px] mx-auto min-h-screen p-[60px] space-y-16">
        <section className="flex justify-start">
          <BtnBackArticle articleId={1} />
        </section>
        <ReviewTagContainer />
      </main>
    </>
  );
};

export default Review;
