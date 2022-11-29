/*
 * 책임 작성자: 박혜정
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-20
 * 개요
   - 채택을 최종적으로 마무리하는 페이지입니다.
 */

import { NextPage } from 'next';
import { useRouter } from 'next/router';
import React, { useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { useRecoilValue, useSetRecoilState } from 'recoil';
import {
  selectedTagsAtom,
  reviewMsgAtom,
  supportMorakAtom,
  supportPayloadAtom,
} from '../../atomsHJ';
import { ProgressBar } from '../../components/hyejung/ProgressBar';
import { BtnBackArticle } from '../../components/hyejung/BtnBackArticle';

const CompletionContainer = () => {
  const selectedTags = useRecoilValue(selectedTagsAtom);
  const message = useRecoilValue(reviewMsgAtom);
  const morak = useRecoilValue(supportMorakAtom);
  const setSupportPayload = useSetRecoilState(supportPayloadAtom);

  const router = useRouter();

  const onClickSetSupportPayload = () => {
    setSupportPayload({
      selectedTags,
      message,
      morak,
    });
    // 추후 후원 api 로 post 요청 로직 들어갈 부분!
    router.replace('/questions/1');
  };

  return (
    <section className="flex justify-between h-full md:border-l sm:space-x-10">
      <ProgressBar pageNumber={3} />
      <section className="flex flex-col space-y-10 w-full">
        <section className="flex space-y-10 sm:space-y-0 sm:space-x-10 flex-col sm:flex-row items-center sm:items-start">
          <section className="flex w-full p-6 h-[400px] justify-center flex-col">
            <div className="text-lg lg:text-4xl flex flex-col w-full space-y-5">
              <strong>채택을 위한 모든 단계가 끝났습니다!</strong>
              <strong>
                <strong className="text-main-orange">소중한 후기 </strong>를
                <strong className="text-main-orange"> 박해커 </strong>
                님께 전달해드리겠습니다.
              </strong>
            </div>
          </section>
        </section>

        <article className="ml-auto text-right space-x-3">
          <button
            className="text-base sm:text-lg font-bold"
            onClick={onClickSetSupportPayload}
          >
            채택 완료!
            <FontAwesomeIcon icon={faChevronRight} className="fa-lg mr-1" />
          </button>
        </article>
      </section>
    </section>
  );
};

const Completion: NextPage = () => {
  const router = useRouter();
  const selectedTags = useRecoilValue(selectedTagsAtom);
  useEffect(() => {
    if (selectedTags[0]?.length === 0) router.replace('/review');
  }, []);

  return (
    <>
      <main className="max-w-[1280px] mx-auto min-h-screen p-[60px] space-y-16">
        <section className="flex justify-start">
          <BtnBackArticle articleId={1} />
        </section>
        <CompletionContainer />
      </main>
    </>
  );
};

export default Completion;
