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
        eventSources={[
          {
            events: [
              {
                title: '카카오모빌리티',
                start: '2022-11-07',
                end: '2022-11-21',
                color: '#FFDF6B',
                textColor: 'black',
              },
              {
                title: '(주)안랩',
                start: '2022-11-07',
                end: '2022-11-20',
                color: '#FFB34D',
                textColor: 'black',
              },
              {
                title: '네이버 파이낸셜',
                start: '2022-12-01',
                end: '2022-12-12',
                color: '#FFDF6B',
                textColor: 'black',
              },
              {
                title: '아고다',
                start: '2022-12-01',
                end: '2022-12-29',
                color: '#FFB34D',
                textColor: 'black',
              },
            ],
          },
        ]}
      />
    </div>
  );
};
