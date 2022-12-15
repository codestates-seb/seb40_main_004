/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-19
 * 최근 수정일: 2022-12-16
 */

import { useEffect, useState } from 'react';
import { useRecoilValue } from 'recoil';
import { userDashboardAtom } from '../../atomsYW';

// y 축은 7 로 나눈 나머지
// x 축은 7 로 나눈 몫 올림 - 1

const arrX: number[] = [];
const arrY: number[] = [];
const arrCreatedNumber: number[][] = [[-1, -1]];

for (let i = 0; i <= 1120; i += 20) {
  arrX.push(i);
}

for (let i = 0; i <= 120; i += 20) {
  arrY.push(i);
}

for (let i = 0; i <= 56; i++) {
  for (let j = 0; j <= 6; j++) {
    arrCreatedNumber.push([i, j]);
  }
}

interface IActicity {
  articleCount: number;
  answerCount: number;
  commentCount: number;
  total: number;
  createdDate: string;
  createdNumber: number;
}

export const Grass = () => {
  const userDashboard = useRecoilValue(userDashboardAtom);
  const [activity, setActivity] = useState<IActicity | null>(null);
  const [rendering, setRendering] = useState(false);

  useEffect(() => {
    const total = userDashboard.activities
      .map((activity) => activity?.total)
      .reduce((acc, cur) => acc + cur, 0);
    const articleCount = userDashboard.activities
      .map((activity) => activity?.articleCount)
      .reduce((acc, cur) => acc + cur, 0);
    const answerCount = userDashboard.activities
      .map((activity) => activity?.answerCount)
      .reduce((acc, cur) => acc + cur, 0);
    const commentCount = userDashboard.activities
      .map((activity) => activity?.commentCount)
      .reduce((acc, cur) => acc + cur, 0);

    setActivity({
      total,
      articleCount,
      answerCount,
      commentCount,
      createdDate: '2022',
      createdNumber: 0,
    });
  }, []);

  const onClick = (activity: IActicity | undefined) => {
    if (activity) setActivity(activity);
  };
  return (
    <>
      <svg className="w-full h-[200px]" viewBox="0 0 1120 180">
        <text
          fontSize="13"
          textAnchor="start"
          x="980"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          12월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="900"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          11월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="800"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          10월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="720"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          9월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="620"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          8월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="520"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          7월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="440"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          6월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="340"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          5월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="260"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          4월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="180"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          3월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="100"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          2월
        </text>
        <text
          fontSize="13"
          textAnchor="start"
          x="0"
          y="160"
          dy="0.3em"
          fill="#8a8f95"
        >
          1월
        </text>
        {arrCreatedNumber.slice(7, 372).map((arr, i) => (
          <rect
            key={i - 6}
            width="18"
            height="18"
            rx="5"
            fill={
              userDashboard.activities
                .map((activity) => activity.createdNumber)
                .includes(i - 6)
                ? '#007950'
                : '#dddfe0'
            }
            strokeWidth="2.5"
            x={`${arrX[arr[0]]}`}
            y={`${arrY[arr[1]]}`}
            className={
              userDashboard.activities
                .map((activity) => activity.createdNumber)
                .includes(i - 6)
                ? 'hover:cursor-pointer'
                : ''
            }
            onClick={
              userDashboard.activities
                .map((activity) => activity.createdNumber)
                .includes(i - 6)
                ? () =>
                    onClick(
                      userDashboard?.activities.find(
                        (activity) => activity.createdNumber === i - 6,
                      ),
                    )
                : undefined
            }
          ></rect>
        ))}
      </svg>
      <div>
        <div className="text-xl">{activity?.createdDate ?? 2022}</div>
        <div>{`총 활동: ${activity?.total ?? 0}`}</div>
        <div>{`게시글 작성: ${activity?.articleCount ?? 0}`}</div>
        <div>{`답변 작성: ${activity?.answerCount ?? 0}`}</div>
        <div>{`댓글 작성: ${activity?.commentCount ?? 0}`}</div>
      </div>
    </>
  );
};
