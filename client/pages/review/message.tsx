/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-30
 * 개요
   - 답변 작성자에 대한 후기 메시지를 작성할 수 있는 페이지입니다.
 */

import { NextPage } from 'next';
import { useRouter } from 'next/router';
import React from 'react';
import { useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { useForm } from 'react-hook-form';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import {
  reviewTagsAtom,
  reviewContentAtom,
  reviewRequestAtom,
} from '../../atomsHJ';
import { ProgressBar } from '../../components/hyejung/ProgressBar';
import { BtnBackArticle } from '../../components/hyejung/BtnBackArticle';

type MessageForm = {
  message: string;
};

const Message: NextPage = () => {
  const router = useRouter();
  const setReviewContent = useSetRecoilState(reviewContentAtom);

  const { register, watch, handleSubmit } = useForm<MessageForm>();
  const formData = watch();

  const onValid = (data: MessageForm) => {
    router.push('/review/support-morak');
    setReviewContent(data.message);
  };

  const reviewTags = useRecoilValue(reviewTagsAtom);

  useEffect(() => {
    if (reviewTags?.length === 1) router.replace('/review');
  }, []);

  const reviewRequest = useRecoilValue(reviewRequestAtom);
  return (
    <>
      <main className="max-w-[1280px] mx-auto min-h-screen p-[60px] space-y-16">
        <section className="flex justify-start">
          <BtnBackArticle articleId={reviewRequest.articleId} />
        </section>
        <section className="flex justify-between h-full md:border-l sm:space-x-10">
          <ProgressBar pageNumber={1} />
          <section className="flex flex-col space-y-10 w-full">
            <article className="text-left space-y-2 flex flex-col">
              <h1 className="text-2xl font-bold text-right">
                💌 후원하실 분에게 자세한 후기/응원메시지를 남겨주세요!
              </h1>
            </article>

            <section className="flex w-full p-6 h-[400px] bg-white rounded-[20px] justify-center flex-col space-y-5">
              <ul className="space-y-3 text-main-gray text-xs md:text-base">
                <span>{`예시)`}</span>
                <li>
                  ▪ 상세한 답변 감사드려요! 남겨주신 답변을 토대로 문제를 잘
                  해결할 수 있었습니다. 감사합니다!
                </li>
                <li>
                  ▪ 응원의 말씀 감사드립니다! 조언해주신 내용을 바탕으로 더
                  발전할 수 있을 것 같습니다.
                </li>
              </ul>

              <form
                className="flex w-full"
                id="message"
                onSubmit={handleSubmit(onValid)}
              >
                <textarea
                  className="w-full rounded-[20px] border p-4 text-sm  focus:outline-main-gray"
                  rows={9}
                  minLength={15}
                  placeholder={'당신만의 따듯한 후기를 작성해주세요!'}
                  {...register('message', {
                    required: '따듯한 후기를 남겨주세요!',
                    minLength: {
                      message: '메시지는 최소 15글자 이상 작성해주세요!',
                      value: 15,
                    },
                  })}
                />
              </form>
            </section>

            <article className="ml-auto text-right space-x-3">
              {!Object.keys(formData).length ||
              formData.message?.length < 15 ? (
                <div className="text-base sm:text-lg font-bold text-main-gray">
                  최소 15자 이상 작성해주세요!
                </div>
              ) : (
                <button
                  className="text-base sm:text-lg font-bold"
                  form="message"
                >
                  다음 단계로!
                  <FontAwesomeIcon
                    icon={faChevronRight}
                    className="fa-lg mr-1"
                  />
                </button>
              )}
            </article>
          </section>
        </section>
      </main>
    </>
  );
};

export default Message;
