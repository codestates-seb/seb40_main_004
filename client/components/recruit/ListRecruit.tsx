import { Loader } from '@components/common/Loader';
import { useFetch } from '@libs/useFetchSWR';
import { ICalendar } from '@type/calendar';

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
