/*
 * 책임 작성자: 박연우
 * 최초 작성일: 2022-11-18
 * 최근 수정일: 2022-11-18
 */

import FullCalendar from '@fullcalendar/react';
import daygridPlugin from '@fullcalendar/daygrid';

export const Calendar = () => {
  return (
    <div>
      <FullCalendar
        headerToolbar={{
          start: 'prev',
          center: 'title',
          end: 'next',
        }}
        plugins={[daygridPlugin]}
        height="568px"
      />
    </div>
  );
};
