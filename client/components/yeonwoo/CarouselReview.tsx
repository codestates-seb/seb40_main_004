/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-11-19
 */

import * as React from 'react';
import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { wrap } from 'popmotion';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faChevronLeft,
  faChevronRight,
} from '@fortawesome/free-solid-svg-icons';

const reviews = [
  {
    author: '박연우',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다. 저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: false,
  },
  {
    author: '2',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: true,
  },
  {
    author: '3',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: true,
  },
  {
    author: '4',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: true,
  },
  {
    author: '5',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: true,
  },
  {
    author: '6',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: true,
  },
  {
    author: '7',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: true,
  },
  {
    author: '8',
    text: '저번에 답변을 너무 자세하게 잘해주셔서 큰 도움이 되었습니다. 감사합니다.',
    like: true,
  },
];

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
  author: string;
  text: string;
  like: boolean;
}

export const CarouselReview = () => {
  const [[page, direction], setPage] = useState([0, 0]);
  const [id, setId] = useState<null | string>(null);
  const [curReview, setCurReview] = useState<review>({
    author: '',
    text: '',
    like: false,
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
              key={review.author}
              layoutId={review.author}
              className="bg-main-yellow bg-opacity-20 w-[215px] h-[215px] rounded-2xl p-8 relative"
            >
              <div>
                <p
                  className="hover:cursor-pointer"
                  onClick={() => {
                    setId(review.author);
                    setCurReview(review);
                  }}
                >
                  {review.text.length > 50
                    ? `${review.text.slice(0, 50)}...`
                    : review.text}
                </p>
              </div>
              <div className="absolute bottom-8 flex justify-between w-[155px]">
                <span>{`- ${review.author}`}</span>
                {review.like && (
                  <button className="bg-main-yellow px-4 rounded-full">
                    ❤️
                  </button>
                )}
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
                  {curReview?.text.length > 500
                    ? `${curReview?.text.slice(0, 500)}...`
                    : curReview?.text}
                </p>
              </div>
              <div className="flex justify-between w-full mt-7">
                <span>{`- ${curReview?.author}`}</span>
                {curReview?.like && (
                  <button className="bg-main-yellow px-4 rounded-full">
                    ❤️
                  </button>
                )}
              </div>
            </motion.div>
          </motion.div>
        ) : null}
      </AnimatePresence>
      <div
        className="absolute top-[45%] z-10 right-0 hover:cursor-pointer"
        onClick={() => paginate(3)}
      >
        <FontAwesomeIcon icon={faChevronRight} size="3x" />
      </div>
      <div
        className="absolute top-[45%] z-10 left-0 hover:cursor-pointer"
        onClick={() => paginate(-3)}
      >
        <FontAwesomeIcon icon={faChevronLeft} size="3x" />
      </div>
    </div>
  );
};
