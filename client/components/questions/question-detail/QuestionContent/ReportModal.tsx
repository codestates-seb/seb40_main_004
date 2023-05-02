import { Button } from '@components/common/Button';
import { ReportProps } from '@type/article';
import React, { SetStateAction, useState } from 'react';

type ReportModalFactor = {
  handleReport: (content: ReportProps) => void;
  setIsModalOpen: React.Dispatch<SetStateAction<boolean>>;
};

export const ReportModal = ({
  handleReport,
  setIsModalOpen,
}: ReportModalFactor) => {
  const ILLEGAL_ADVERTISING = 'ILLEGAL_ADVERTISING';
  const BAD_LANGUAGE = 'BAD_LANGUAGE';

  // 신고 사유, 신고 내용 state로 관리
  const [reportReason, setReportReason] = useState({
    reason: 'ILLEGAL_ADVERTISING',
    content: '',
  });

  const handleOnChangeReason = (e: React.ChangeEvent<HTMLInputElement>) => {
    const reason = e.target.value;
    const newReason = { ...reportReason, reason };
    setReportReason(newReason);
  };

  const handleOnChageContent = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const content = e.target.value;
    const newReason = {
      ...reportReason,
      content,
    };
    setReportReason(newReason);
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    handleReport(reportReason);
  };

  return (
    <section className="absolute top-24 left-0 right-0 m-auto px-10 py-5 bg-background-gray/[.90] flex justify-center items-center flex-col">
      <p className="text-lg font-semibold">🔥신고하기</p>
      <form className="w-full flex flex-col space-y-2" onSubmit={handleSubmit}>
        <article className="flex flex-col space-y-1">
          <p className="font-semibold">신고 사유</p>
          <div>
            <input
              type="radio"
              name="reason"
              id={ILLEGAL_ADVERTISING}
              value={ILLEGAL_ADVERTISING}
              checked={reportReason.reason === ILLEGAL_ADVERTISING}
              onChange={handleOnChangeReason}
            />
            <label htmlFor={ILLEGAL_ADVERTISING}> 광고성 게시글</label>
          </div>

          <div>
            <input
              type="radio"
              name="reason"
              id={BAD_LANGUAGE}
              value={BAD_LANGUAGE}
              checked={reportReason.reason === BAD_LANGUAGE}
              onChange={handleOnChangeReason}
            />
            <label htmlFor={BAD_LANGUAGE}> 기타 사유</label>
          </div>
        </article>
        <article className="flex flex-col space-y-1">
          <p className="font-semibold">신고 내용</p>
          <textarea
            onChange={handleOnChageContent}
            value={reportReason.content}
            className="border"
          />
        </article>

        <article className="self-center flex space-x-3">
          <Button>신고하기</Button>
          <button onClick={() => setIsModalOpen(false)}>취소</button>
        </article>
      </form>
    </section>
  );
};
