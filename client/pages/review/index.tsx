import { GetServerSideProps, NextPage } from 'next';
import Link from 'next/link';
import { useState, useEffect } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faChevronRight } from '@fortawesome/free-solid-svg-icons';
import { useRecoilState, useRecoilValue } from 'recoil';

import { getIsFromDashboard } from '@libs/getIsFromDashboard';

import { Seo } from '@components/common/Seo';
import { BtnBackArticle } from '@components/review/BtnBackArticle';
import { ProgressBar } from '@components/review/ProgressBar';
import { ReviewTag } from '@components/review/ReviewTag';

import {
  reviewRequestAtom,
  reviewTagsAtom,
  reviewTagsEnumAtom,
} from '@atoms/reviewAtom';
import { userDashboardAtom } from '@atoms/userAtom';

type ReviewPageProps = {
  prevUrl: string;
};

const Review: NextPage<ReviewPageProps> = ({ prevUrl }) => {
  const [reviewRequest, setReviewRequest] = useRecoilState(reviewRequestAtom);
  const [reviewTags, setReviewTagsAtom] = useRecoilState(reviewTagsAtom);
  const userDashboardInfo = useRecoilValue(userDashboardAtom);
  const tags = useRecoilValue(reviewTagsEnumAtom);
  const [isSelectable, setIsSelectable] = useState(true);

  useEffect(() => {
    const prevUrlResult = getIsFromDashboard(prevUrl);
    if (prevUrlResult) {
      setReviewRequest({
        targetId: Number(prevUrlResult[1]),
        articleId: '',
        targetUserName: userDashboardInfo.nickname,
        dashboardUrl: prevUrl,
      });
    }

    setReviewTagsAtom([{ badgeId: 0, name: '' }]);
  }, []);

  useEffect(() => {
    if (reviewTags.length === 4) setIsSelectable(false);
    else setIsSelectable(true);
  }, [reviewTags]);

  return (
    <>
      <Seo title="응원/후원" />
      <main className="max-w-[1280px] mx-auto min-h-screen p-[60px] space-y-16">
        <section className="flex justify-start">
          <BtnBackArticle articleId={reviewRequest.articleId} />
        </section>
        <section className="flex justify-between h-full md:border-l sm:space-x-10">
          <ProgressBar pageNumber={0} />
          <section className="flex flex-col space-y-10 w-full">
            <article className="text-left space-y-2 flex flex-col">
              <h1 className="text-2xl font-bold text-right">
                🔖후원하실 분을 설명할 수 있는 태그를 골라주세요!
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

export const getServerSideProps: GetServerSideProps = async (ctx) => {
  const prevUrl = ctx.req.headers.referer ?? null;
  return {
    props: { prevUrl },
  };
};

export default Review;
