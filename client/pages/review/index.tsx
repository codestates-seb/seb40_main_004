/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-30
 * 개요
   - 답변을 채택할 때 리뷰 태그를 선택할 수 있는 페이지 입니다.
 */

import { NextPage } from 'next';
import Link from 'next/link';
import React, { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { useRecoilState, useRecoilValue } from 'recoil';
import {
  reviewRequestAtom,
  reviewTagsAtom,
  reviewTagsEnumAtom,
} from '../../atomsHJ';
import { ReviewTag } from '../../components/hyejung/ReviewTag';
import { ProgressBar } from '../../components/hyejung/ProgressBar';
import { BtnBackArticle } from '../../components/hyejung/BtnBackArticle';
import { useRouter } from 'next/router';

const Review: NextPage = () => {
  const router = useRouter();
  const reviewRequest = useRecoilValue(reviewRequestAtom);
  const [reviewTags, setReviewTagsAtom] = useRecoilState(reviewTagsAtom);
  const [isSelectable, setIsSelectable] = useState(true);
  const tags = useRecoilValue(reviewTagsEnumAtom);

  useEffect(() => {
    if (!reviewRequest.articleId) {
      alert('잘못된 접근입니다!');
      router.replace('/');
    }
    setReviewTagsAtom([{ badgeId: 0, name: '' }]);
  }, []);

  useEffect(() => {
    if (reviewTags.length === 4) setIsSelectable(false);
    else setIsSelectable(true);
  }, [reviewTags]);

  return (
    <>
      <main className="max-w-[1280px] mx-auto min-h-screen p-[60px] space-y-16">
        <section className="flex justify-start">
          <BtnBackArticle articleId={reviewRequest.articleId} />
        </section>
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
                  <ReviewTag
                    key={tag[1]}
                    isSelectable={isSelectable}
                    enumTag={tag[1]}
                  >
                    {tag[0]}
                  </ReviewTag>
                ))}
              </article>
            </section>

            <article className="ml-auto text-right space-x-3">
              {reviewTags.length > 1 ? (
                <Link href={'/review/message'}>
                  <button className="text-base sm:text-lg font-bold">
                    다음 단계로!
                    <FontAwesomeIcon
                      icon={faChevronRight}
                      className="fa-lg mr-1"
                    />
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
      </main>
    </>
  );
};

export default Review;
