package whs.vmc.prak1.lib;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.message.BasicHeader;

public class MyCellIDRequestEntity implements HttpEntity
{
     protected int myCellID;
     protected int myLAC;

     public MyCellIDRequestEntity(int aCellID, int aLAC)
     {
          this.myCellID = aCellID;
          this.myLAC = aLAC;
     }

     public Header getContentType()
     {
        return new BasicHeader("Content-Type", "application/binary");
     }

   public void consumeContent() throws IOException
   {
   }

   public InputStream getContent() throws IOException, IllegalStateException
   {
      return null;
   }

   public Header getContentEncoding()
   {
      return null;
   }

   public boolean isChunked()
   {
      return false;
   }

   public boolean isStreaming()
   {
      return false;
   }

    /** Pretend to be a French Sony_Ericsson-K750 that
     * wants to receive its lat/long-values =)
     * The data written is highly proprietary !!! */
   public void writeTo(OutputStream outputStream) throws IOException
   {
        DataOutputStream os = new DataOutputStream(outputStream);
        os.writeShort(21);
        os.writeLong(0);
        os.writeUTF("fr");
        os.writeUTF("Sony_Ericsson-K750");
        os.writeUTF("1.3.1");
        os.writeUTF("Web");
        os.writeByte(27);

        os.writeInt(0); os.writeInt(0); os.writeInt(3);
        os.writeUTF("");
        os.writeInt(myCellID); // CELL-ID
        os.writeInt(myLAC); // LAC
        os.writeInt(0); os.writeInt(0);
        os.writeInt(0); os.writeInt(0);
        os.flush();
   }

   @Override
   public long getContentLength()
   {
      return -1;
   }

   @Override
   public boolean isRepeatable()
   {
      return true;
   } 
}