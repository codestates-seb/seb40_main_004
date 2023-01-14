/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-12-07
 * 최근 수정일: 2022-12-07
 */

import Link from 'next/link';
import { ICalendar } from '../../interfaces';
import { useFetch } from '../../libs/useFetchSWR';
import { Loader } from '../common/Loader';

export const ListRecruit = () => {
  const {
    data: response,
    isLoading,
  }: { data: ICalendar[]; isLoading: boolean } = useFetch(`/api/calendars`);
  return (
    <>
      {!isLoading ? (
        <div className="grid grid-cols-3 gap-x-8 gap-y-10">
          {response.map((recruit) => (
            <a target="_blank" rel="noopener" href={recruit.url}>
              <div
                key={recruit.jobId}
                className="bg-main-yellow bg-opacity-30 border border-main-gray rounded-3xl px-4 py-8 hover:cursor-pointer hover:bg-opacity-10"
              >
                <div className="font-bold text-lg mb-4">{recruit.name}</div>
                <div className="text-sm mb-1">{recruit.careerRequirement}</div>
                <div className="text-xs">{`${recruit.startDate} - ${recruit.endDate}`}</div>
              </div>
            </a>
          ))}
        </div>
      ) : (
        <div className="flex justify-center">
          <Loader />
        </div>
      )}
    </>
  );
};
