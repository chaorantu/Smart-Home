#include <afx.h>
//#include <iostream.h>
#include <afxcoll.h>

class CSortStringArray : public CStringArray {
public:
   void Sort();
private:
   BOOL CompareAndSwap(int pos);
};
void CSortStringArray::Sort()
{
   BOOL bNotDone = TRUE;

   while (bNotDone)
   {
      bNotDone = FALSE;
      for(int pos = 0;pos < GetUpperBound();pos++)
         bNotDone |= CompareAndSwap(pos);
   }
}
BOOL CSortStringArray::CompareAndSwap(int pos)
{
   CString temp;
   int posFirst = pos;
   int posNext = pos + 1;

   if (GetAt(posFirst).CompareNoCase(GetAt(posNext)) > 0)
   {
      temp = GetAt(posFirst);
      SetAt(posFirst, GetAt(posNext));
      SetAt(posNext, temp);
      return TRUE;

   }
   return FALSE;
}