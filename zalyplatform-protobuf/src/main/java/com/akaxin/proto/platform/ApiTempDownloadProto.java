// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: platform/api_temp_download.proto

package com.akaxin.proto.platform;

public final class ApiTempDownloadProto {
  private ApiTempDownloadProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ApiTempDownloadRequestOrBuilder extends
      // @@protoc_insertion_point(interface_extends:platform.ApiTempDownloadRequest)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *临时空间的位置名称&lt;key&gt;
     * </pre>
     *
     * <code>optional string name = 1;</code>
     */
    java.lang.String getName();
    /**
     * <pre>
     *临时空间的位置名称&lt;key&gt;
     * </pre>
     *
     * <code>optional string name = 1;</code>
     */
    com.google.protobuf.ByteString
        getNameBytes();
  }
  /**
   * Protobuf type {@code platform.ApiTempDownloadRequest}
   */
  public  static final class ApiTempDownloadRequest extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:platform.ApiTempDownloadRequest)
      ApiTempDownloadRequestOrBuilder {
    // Use ApiTempDownloadRequest.newBuilder() to construct.
    private ApiTempDownloadRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ApiTempDownloadRequest() {
      name_ = "";
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private ApiTempDownloadRequest(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              name_ = s;
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest.class, com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest.Builder.class);
    }

    public static final int NAME_FIELD_NUMBER = 1;
    private volatile java.lang.Object name_;
    /**
     * <pre>
     *临时空间的位置名称&lt;key&gt;
     * </pre>
     *
     * <code>optional string name = 1;</code>
     */
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      }
    }
    /**
     * <pre>
     *临时空间的位置名称&lt;key&gt;
     * </pre>
     *
     * <code>optional string name = 1;</code>
     */
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, name_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, name_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest)) {
        return super.equals(obj);
      }
      com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest other = (com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest) obj;

      boolean result = true;
      result = result && getName()
          .equals(other.getName());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code platform.ApiTempDownloadRequest}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:platform.ApiTempDownloadRequest)
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequestOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadRequest_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadRequest_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest.class, com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest.Builder.class);
      }

      // Construct using com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        name_ = "";

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadRequest_descriptor;
      }

      public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest getDefaultInstanceForType() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest.getDefaultInstance();
      }

      public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest build() {
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest buildPartial() {
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest result = new com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest(this);
        result.name_ = name_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest) {
          return mergeFrom((com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest other) {
        if (other == com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest.getDefaultInstance()) return this;
        if (!other.getName().isEmpty()) {
          name_ = other.name_;
          onChanged();
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object name_ = "";
      /**
       * <pre>
       *临时空间的位置名称&lt;key&gt;
       * </pre>
       *
       * <code>optional string name = 1;</code>
       */
      public java.lang.String getName() {
        java.lang.Object ref = name_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          name_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <pre>
       *临时空间的位置名称&lt;key&gt;
       * </pre>
       *
       * <code>optional string name = 1;</code>
       */
      public com.google.protobuf.ByteString
          getNameBytes() {
        java.lang.Object ref = name_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          name_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       *临时空间的位置名称&lt;key&gt;
       * </pre>
       *
       * <code>optional string name = 1;</code>
       */
      public Builder setName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *临时空间的位置名称&lt;key&gt;
       * </pre>
       *
       * <code>optional string name = 1;</code>
       */
      public Builder clearName() {
        
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <pre>
       *临时空间的位置名称&lt;key&gt;
       * </pre>
       *
       * <code>optional string name = 1;</code>
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        name_ = value;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:platform.ApiTempDownloadRequest)
    }

    // @@protoc_insertion_point(class_scope:platform.ApiTempDownloadRequest)
    private static final com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest();
    }

    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ApiTempDownloadRequest>
        PARSER = new com.google.protobuf.AbstractParser<ApiTempDownloadRequest>() {
      public ApiTempDownloadRequest parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new ApiTempDownloadRequest(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ApiTempDownloadRequest> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ApiTempDownloadRequest> getParserForType() {
      return PARSER;
    }

    public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadRequest getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface ApiTempDownloadResponseOrBuilder extends
      // @@protoc_insertion_point(interface_extends:platform.ApiTempDownloadResponse)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     *下载的具体内容
     * </pre>
     *
     * <code>optional bytes content = 1;</code>
     */
    com.google.protobuf.ByteString getContent();
  }
  /**
   * Protobuf type {@code platform.ApiTempDownloadResponse}
   */
  public  static final class ApiTempDownloadResponse extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:platform.ApiTempDownloadResponse)
      ApiTempDownloadResponseOrBuilder {
    // Use ApiTempDownloadResponse.newBuilder() to construct.
    private ApiTempDownloadResponse(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ApiTempDownloadResponse() {
      content_ = com.google.protobuf.ByteString.EMPTY;
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private ApiTempDownloadResponse(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 10: {

              content_ = input.readBytes();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadResponse_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadResponse_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse.class, com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse.Builder.class);
    }

    public static final int CONTENT_FIELD_NUMBER = 1;
    private com.google.protobuf.ByteString content_;
    /**
     * <pre>
     *下载的具体内容
     * </pre>
     *
     * <code>optional bytes content = 1;</code>
     */
    public com.google.protobuf.ByteString getContent() {
      return content_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!content_.isEmpty()) {
        output.writeBytes(1, content_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!content_.isEmpty()) {
        size += com.google.protobuf.CodedOutputStream
          .computeBytesSize(1, content_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse)) {
        return super.equals(obj);
      }
      com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse other = (com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse) obj;

      boolean result = true;
      result = result && getContent()
          .equals(other.getContent());
      return result;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      hash = (37 * hash) + CONTENT_FIELD_NUMBER;
      hash = (53 * hash) + getContent().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code platform.ApiTempDownloadResponse}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:platform.ApiTempDownloadResponse)
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponseOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadResponse_descriptor;
      }

      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadResponse_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse.class, com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse.Builder.class);
      }

      // Construct using com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        content_ = com.google.protobuf.ByteString.EMPTY;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.internal_static_platform_ApiTempDownloadResponse_descriptor;
      }

      public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse getDefaultInstanceForType() {
        return com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse.getDefaultInstance();
      }

      public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse build() {
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse buildPartial() {
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse result = new com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse(this);
        result.content_ = content_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse) {
          return mergeFrom((com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse other) {
        if (other == com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse.getDefaultInstance()) return this;
        if (other.getContent() != com.google.protobuf.ByteString.EMPTY) {
          setContent(other.getContent());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private com.google.protobuf.ByteString content_ = com.google.protobuf.ByteString.EMPTY;
      /**
       * <pre>
       *下载的具体内容
       * </pre>
       *
       * <code>optional bytes content = 1;</code>
       */
      public com.google.protobuf.ByteString getContent() {
        return content_;
      }
      /**
       * <pre>
       *下载的具体内容
       * </pre>
       *
       * <code>optional bytes content = 1;</code>
       */
      public Builder setContent(com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        content_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       *下载的具体内容
       * </pre>
       *
       * <code>optional bytes content = 1;</code>
       */
      public Builder clearContent() {
        
        content_ = getDefaultInstance().getContent();
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:platform.ApiTempDownloadResponse)
    }

    // @@protoc_insertion_point(class_scope:platform.ApiTempDownloadResponse)
    private static final com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse();
    }

    public static com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ApiTempDownloadResponse>
        PARSER = new com.google.protobuf.AbstractParser<ApiTempDownloadResponse>() {
      public ApiTempDownloadResponse parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new ApiTempDownloadResponse(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ApiTempDownloadResponse> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ApiTempDownloadResponse> getParserForType() {
      return PARSER;
    }

    public com.akaxin.proto.platform.ApiTempDownloadProto.ApiTempDownloadResponse getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_platform_ApiTempDownloadRequest_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_platform_ApiTempDownloadRequest_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_platform_ApiTempDownloadResponse_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_platform_ApiTempDownloadResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n platform/api_temp_download.proto\022\010plat" +
      "form\"&\n\026ApiTempDownloadRequest\022\014\n\004name\030\001" +
      " \001(\t\"*\n\027ApiTempDownloadResponse\022\017\n\007conte" +
      "nt\030\001 \001(\0142i\n\026ApiTempDownloadService\022O\n\010do" +
      "wnload\022 .platform.ApiTempDownloadRequest" +
      "\032!.platform.ApiTempDownloadResponseB1\n\031c" +
      "om.akaxin.proto.platformB\024ApiTempDownloa" +
      "dProtob\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_platform_ApiTempDownloadRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_platform_ApiTempDownloadRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_platform_ApiTempDownloadRequest_descriptor,
        new java.lang.String[] { "Name", });
    internal_static_platform_ApiTempDownloadResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_platform_ApiTempDownloadResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_platform_ApiTempDownloadResponse_descriptor,
        new java.lang.String[] { "Content", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
