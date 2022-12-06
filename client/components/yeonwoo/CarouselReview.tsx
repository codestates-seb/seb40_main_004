/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-12-05
 */

import * as React from 'react';
import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { wrap } from 'popmotion';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChevronLeft,
  faChevronRight,
} from '@fortawesome/free-solid-svg-icons';
import { useRecoilValue } from 'recoil';
import { userDashboardAtom } from '../../atomsYW';

const variants = {
  enter: (direction: number) => {
    return {
      x: direction > 0 ? 1000 : -1000,
      opacity: 0,
    };
  },
  center: {
    zIndex: 1,
    x: 0,
    opacity: 1,
  },
  exit: (direction: number) => {
    return {
      zIndex: 0,
      x: direction < 0 ? 1000 : -1000,
      opacity: 0,
    };
  },
};

/**
 * Experimenting with distilling swipe offset and velocity into a single variable, so the
 * less distance a user has swiped, the more velocity they need to register as a swipe.
 * Should accomodate longer swipes and short flicks without having binary checks on
 * just distance thresholds and velocity > 0.
 */
const swipeConfidenceThreshold = 10000;
const swipePower = (offset: number, velocity: number) => {
  return Math.abs(offset) * velocity;
};

interface review {
  reviewId: number;
  content: string;
  createdAt: string;
  userInfo: {
    userId: number;
    nickname: string;
    grade: string;
  };
}

export const CarouselReview = () => {
  const { reviews } = useRecoilValue(userDashboardAtom);
  const [[page, direction], setPage] = useState([0, 0]);
  const [id, setId] = useState<null | string>(null);
  const [curReview, setCurReview] = useState<review>({
    reviewId: 0,
    content: '',
    createdAt: '',
    userInfo: {
      userId: 0,
      nickname: '',
      grade: '',
    },
  });

  // We only have 3 images, but we paginate them absolutely (ie 1, 2, 3, 4, 5...) and
  // then wrap that within 0-2 to find our image ID in the array below. By passing an
  // absolute page index as the `motion` component's `key` prop, `AnimatePresence` will
  // detect it as an entirely new image. So you can infinitely paginate as few as 1 images.
  const reviewIndex = wrap(0, Math.ceil(reviews.length / 3) * 3, page);

  const paginate = (newDirection: number) => {
    setPage([page + newDirection, newDirection]);
  };

  return (
    <div className="relative w-full h-[190px]">
      {reviews.length !== 0 ? (
        <>
          <AnimatePresence initial={false} custom={direction}>
            <motion.div
              key={page}
              className="w-full h-[190px] absolute px-24 flex gap-[72px]"
              custom={direction}
              variants={variants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{
                x: { type: 'spring', stiffness: 300, damping: 30 },
              }}
              drag="x"
              dragConstraints={{ left: 0, right: 0 }}
              dragElastic={1}
              onDragEnd={(e, { offset, velocity }) => {
                const swipe = swipePower(offset.x, velocity.x);

                if (swipe < -swipeConfidenceThreshold) {
                  paginate(3);
                } else if (swipe > swipeConfidenceThreshold) {
                  paginate(-3);
                }
              }}
            >
              {reviews.slice(reviewIndex, reviewIndex + 3).map((review) => (
                <motion.div
                  key={review.reviewId}
                  layoutId={review.reviewId + ''}
                  className="bg-main-yellow bg-opacity-20 w-[215px] h-[215px] rounded-2xl p-8 relative hover:cursor-pointer"
                  onClick={() => {
                    setCurReview(review);
                    setTimeout(() => {
                      setId(review.reviewId + '');
                    }, 50);
                  }}
                >
                  <div>
                    <p>
                      {review.content.length > 35
                        ? `${review.content.slice(0, 35)}...`
                        : review.content}
                    </p>
                  </div>
                  <div className="absolute bottom-8 flex justify-between w-[155px]">
                    <span>{`- ${review.userInfo.nickname}`}</span>
                  </div>
                </motion.div>
              ))}
            </motion.div>
            {id ? (
              <motion.div
                className="w-screen h-screen fixed top-0 left-0 flex justify-center items-center z-10"
                onClick={() => setId(null)}
                initial={{ backgroundColor: 'rgba(0,0,0,0)' }}
                animate={{ backgroundColor: 'rgba(0,0,0,0.5)' }}
                exit={{ backgroundColor: 'rgba(0,0,0,0)' }}
              >
                <motion.div
                  layoutId={id}
                  className="w-[633px] h-[633px] bg-[#F5EFD9] p-12 rounded-2xl text-[28px]"
                >
                  <div>
                    <p>
                      {curReview?.content.length > 500
                        ? `${curReview?.content.slice(0, 500)}...`
                        : curReview?.content}
                    </p>
                  </div>
                  <div className="flex justify-between w-full mt-7">
                    <span>{`- ${curReview?.userInfo.nickname}`}</span>
                  </div>
                </motion.div>
              </motion.div>
            ) : null}
          </AnimatePresence>
          <div
            className="absolute top-[45%] z-10 right-0 hover:cursor-pointer"
            onClick={() => paginate(3)}
          >
            <FontAwesomeIcon icon={faChevronRight} size="2xl" />
          </div>
          <div
            className="absolute top-[45%] z-10 left-0 hover:cursor-pointer"
            onClick={() => paginate(-3)}
          >
            <FontAwesomeIcon icon={faChevronLeft} size="2xl" />
          </div>
        </>
      ) : (
        <div className="w-full h-full flex justify-center items-center">
          <span>받은 응원 메세지가 아직 없습니다</span>
        </div>
      )}
    </div>
  );
};
