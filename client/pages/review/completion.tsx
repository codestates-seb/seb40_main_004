import { NextPage } from 'next';
import { useRouter } from 'next/router';
import { useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { useRecoilValue } from 'recoil';

import {
  reviewTagsAtom,
  reviewContentAtom,
  reviewPointAtom,
  reviewRequestAtom,
} from '@atoms/reviewAtom';

import { ProgressBar } from '@components/review/ProgressBar';
import { BtnBackArticle } from '@components/review/BtnBackArticle';
import { Seo } from '@components/common/Seo';

import { client } from '@libs/client';
import { toast } from 'react-toastify';

const Completion: NextPage = () => {
  const router = useRouter();
  const reviewTags = useRecoilValue(reviewTagsAtom);
  const reviewContent = useRecoilValue(reviewContentAtom);
  const reviewPoint = useRecoilValue(reviewPointAtom);
  const reviewRequest = useRecoilValue(reviewRequestAtom);

  useEffect(() => {
    if (reviewTags?.length === 0) router.replace('/review');
  }, []);

  const onClickSetSupportPayload = () => {
    const url = reviewRequest.dashboardUrl
      ? `/api/users/${reviewRequest.targetId}/reviews`
      : `/api/articles/${reviewRequest.articleId}/answers/${reviewRequest.targetId}/reviews`;

    const payload = {
      content: reviewContent,
      badges: reviewTags.slice(1),
      point: reviewPoint,
    };

    client
      .post(url, payload)
      .then((res) => {
        // console.log(res.data);
        toast.success('🔥후기가 전송되었습니다! 따듯한 후기 고마워요!🔥');
        if (reviewRequest.dashboardUrl) {
          router.replace(reviewRequest.dashboardUrl);
        } else {
          router.replace(`/questions/${reviewRequest.articleId}`);
        }
      })
      .catch((err) => {
        toast.error('답변 채택에 실패했습니다.🥲');
        console.log(err);
      });
  };

  return (
    <>
      <Seo title="응원/후원 - 완료" />
      <main className="max-w-[1280px] mx-auto min-h-screen p-[60px] space-y-16">
        <section className="flex justify-start">
          <BtnBackArticle articleId={reviewRequest.articleId} />
        </section>
        <section className="flex justify-between h-full md:border-l sm:space-x-10">
          <ProgressBar pageNumber={3} />
          <section className="flex flex-col space-y-10 w-full">
            <section className="flex space-y-10 sm:space-y-0 sm:space-x-10 flex-col sm:flex-row items-center sm:items-start">
              <section className="flex w-full p-6 h-[400px] justify-center flex-col">
                <div className="text-lg lg:text-4xl flex flex-col w-full space-y-5">
                  <strong>모든 단계가 끝났습니다!🔥</strong>
                  <strong>
                    <strong className="text-main-orange">소중한 후기 </strong>를
                    <strong className="text-main-orange">
                      {' '}
                      {reviewRequest.targetUserName}{' '}
                    </strong>
                    님께 전달해드리겠습니다.
                  </strong>
                </div>
              </section>
            </section>

            {reviewRequest.dashboardUrl ? (
              <article className="ml-auto text-right space-x-3">
                <span className="text-xs text-main-gray mr-3">
                  남기신 후기는 취소하실 수 없습니다.
                </span>
                <button
                  className="text-base sm:text-lg font-bold"
                  onClick={onClickSetSupportPayload}
                >
                  후기 남기기 완료!
                  <FontAwesomeIcon
                    icon={faChevronRight}
                    className="fa-lg mr-1"
                  />
                </button>
              </article>
            ) : (
              <article className="ml-auto text-right space-x-3">
                <span className="text-xs text-main-gray mr-3">
                  채택 완료 후에는 채택을 취소하실 수 없습니다.
                </span>
                <button
                  className="text-base sm:text-lg font-bold"
                  onClick={onClickSetSupportPayload}
                >
                  채택 완료!
                  <FontAwesomeIcon
                    icon={faChevronRight}
                    className="fa-lg mr-1"
                  />
                </button>
              </article>
            )}
          </section>
        </section>
      </main>
    </>
  );
};

export default Completion;
